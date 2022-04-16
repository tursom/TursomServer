package cn.tursom.core

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.pool.HeapMemoryPool
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * 原理 https://www.cnblogs.com/lidabo/p/9018548.html
 */
class RewriteFlvChecker {
  companion object {
    val pool = HeapMemoryPool(16 * 1024 * 1024)
  }


  /**
   * 从头部开始Check, 重新锚定时间戳
   */
  fun check(inputStream: InputStream, outputStream: OutputStream) {
    // 用于统计时间戳
    val lastTimestampRead = intArrayOf(-1, -1)
    val lastTimestampWrite = intArrayOf(-1, -1)

    // 复制头部
    val header = ByteArray(9)
    inputStream.read(header)
    outputStream.write(header)
    try {
      @Suppress("ControlFlowWithEmptyBody")
      while (handleFrame(inputStream, outputStream, lastTimestampRead, lastTimestampWrite));
    } finally {
      inputStream.close()
      outputStream.close()
    }
  }


  private val skipBuf = ByteArray(4)
  private val zeroTimestamp = byteArrayOf(0, 0, 0, 0)
  private fun handleFrame(
    inputStream: InputStream,
    outputStream: OutputStream,
    lastTimestampRead: IntArray,
    lastTimestampWrite: IntArray,
  ): Boolean {
    // 读取前一个tag size
    val frame = pool.get()
    frame.put(inputStream, 4)
    // 读取tag
    frame.use {
      when (val tagType = inputStream.read()) {
        8, 9 -> {
          // tag data size 3个字节。表示tag data的长度。从streamd id 后算起。
          val dataSize = readBytesToInt(frame, inputStream)
          frame.putByte(tagType.toByte())
          // 时间戳 3
          val timestamp = readBytesToInt(HeapByteBuffer(3), inputStream) + (inputStream.read() shl 24)
          //timestamp += timestampEx
          dealTimestamp(frame, timestamp, tagType - 8, lastTimestampRead, lastTimestampWrite)
          frame.resize(frame.readable + 3 + dataSize)
          frame.put(inputStream, 3 + dataSize)

          outputStream.write(frame.array, frame.readOffset, frame.readable)
        }
        18 -> {
          // 18 scripts
          // 如果是scripts脚本，默认为第一个tag，此时将前一个tag Size 置零
          frame.clear()

          frame.put(zeroTimestamp)
          frame.putByte(tagType.toByte())
          val dataSize = readBytesToInt(frame, inputStream)
          println("data size: $dataSize")
          inputStream.read(skipBuf)
          //frame.put(inputStream, 4)
          frame.put(zeroTimestamp)
          println("frame before: $frame")
          frame.put(inputStream, 3 + dataSize)
          println("frame after: $frame")

          outputStream.write(frame.array, frame.readOffset, frame.readable)
        }
        else -> {
          System.err.println("unsupported tag type: $tagType")
          return false
        }
      }
    }
    return true
  }

  /**
   * 处理音/视频时间戳
   */
  private fun dealTimestamp(
    buffer: ByteBuffer,
    timestamp: Int,
    tagType: Int,
    lastTimestampRead: IntArray,
    lastTimestampWrite: IntArray,
  ) {
    // 如果是首帧
    if (lastTimestampRead[tagType] == -1) {
      lastTimestampWrite[tagType] = 0
    } else if (timestamp >= lastTimestampRead[tagType]) {
      // 如果时序正常
      // 间隔十分巨大(1s)，那么重新开始即可
      if (timestamp > lastTimestampRead[tagType] + 1000) {
        lastTimestampWrite[tagType] += 10
      } else {
        lastTimestampWrite[tagType] = timestamp - lastTimestampRead[tagType] + lastTimestampWrite[tagType]
      }
    } else {
      // 如果出现倒序时间戳
      // 如果间隔不大，那么如实反馈
      if (lastTimestampRead[tagType] - timestamp < 5 * 1000) {
        var tmp = timestamp - lastTimestampRead[tagType] + lastTimestampWrite[tagType]
        tmp = if (tmp > 0) tmp else 1
        lastTimestampWrite[tagType] = tmp
      } else {
        // 间隔十分巨大，那么重新开始即可
        lastTimestampWrite[tagType] += 10
      }
    }
    lastTimestampRead[tagType] = timestamp
    // 低于0xffffff部分
    val lowCurrentTime = lastTimestampWrite[tagType] and 0xffffff
    buffer.putBytes(int2Bytes(lowCurrentTime), 1, 4)
    // 高于0xffffff部分
    val highCurrentTime = lastTimestampWrite[tagType] shr 24
    buffer.putByte(highCurrentTime.toByte())
  }

  private fun readBytesToInt(buffer: ByteBuffer, raf: InputStream): Int {
    var result = 0
    for (i in 0 until 3) {
      val byte = raf.read()
      buffer.putByte(byte.toByte())
      result = (result shl 8) or (byte and 0xff)
    }
    return result
  }

  private fun int2Bytes(value: Int): ByteArray {
    val byteRet = ByteArray(4)
    for (i in 0..3) {
      byteRet[3 - i] = (value shr 8 * i and 0xff).toByte()
    }
    return byteRet
  }
}

fun main() {
  val checker = RewriteFlvChecker()
  checker.check(
    File("test.flv").inputStream().buffered(),
    File("1.flv").outputStream().buffered()
  )
}