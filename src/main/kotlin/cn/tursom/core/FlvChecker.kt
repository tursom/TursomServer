package cn.tursom.core

import java.io.*

/**
 * 原理 https://www.cnblogs.com/lidabo/p/9018548.html
 */
class FlvChecker {
  // 用于缓冲
  private val buffer = ByteArray(1024 * 1024 * 16)

  /**
   * 从头部开始Check, 重新锚定时间戳, 将最后一帧(不管是否完整)去掉
   *
   * @param path
   * @throws IOException
   */
// 用于统计时间戳
  private val lastTimestampRead = intArrayOf(-1, -1)
  private val lastTimestampWrite = intArrayOf(-1, -1)

  /**
   * 从头部开始Check, 重新锚定时间戳, 将最后一帧(不管是否完整)去掉
   */
  fun check(raf: InputStream, rafNew: OutputStream) { // 用于排除无效尾巴帧
    // 复制头部
    raf.read(buffer, 0, 9)
    rafNew.write(buffer, 0, 9)
    try {
      //var remain = 40
      //var timestamp = 0
      loop@ while (true) {
        //remain--
        // 读取前一个tag size
        readBytesToInt(raf, 4)
        // Logger.print("前一个长度为：" + predataSize);
        // 读取tag
        // tag 类型
        when (val tagType = raf.read()) {
          8, 9 -> {
            rafNew.write(buffer, 0, 4)
            rafNew.write(tagType)
            // tag data size 3个字节。表示tag data的长度。从streamd id 后算起。
            val dataSize = readBytesToInt(raf, 3)
            rafNew.write(buffer, 0, 3)
            // 时间戳 3
            val timestamp = readBytesToInt(raf, 3) + (raf.read() shl 24)
            //timestamp += timestampEx
            dealTimestamp(rafNew, timestamp, tagType - 8)
            raf.read(buffer, 0, 3 + dataSize)
            rafNew.write(buffer, 0, 3 + dataSize)
          }
          18 -> {
            // 18 scripts
            // 如果是scripts脚本，默认为第一个tag，此时将前一个tag Size 置零
            val zeroTimestamp = byteArrayOf(0, 0, 0, 0)
            rafNew.write(zeroTimestamp)
            rafNew.write(tagType)
            val dataSize = readBytesToInt(raf, 3)
            rafNew.write(buffer, 0, 3)
            raf.read(buffer, 0, 4)
            val zeros = byteArrayOf(0, 0, 0)
            rafNew.write(zeros) // 时间戳 0
            rafNew.write(0) // 时间戳扩展 0
            raf.read(buffer, 0, 3 + dataSize)
            rafNew.write(buffer, 0, 3 + dataSize)

          }
          else -> {
            break@loop
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  /**
   * 处理音/视频时间戳
   *
   * @param raf
   * @param timestamp
   * @throws IOException
   * @return 是否忽略该tag
   */
  private fun dealTimestamp(raf: OutputStream, timestamp: Int, tagType: Int): Boolean {
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
    raf.write(int2Bytes(lowCurrentTime), 1, 3)
    // 高于0xffffff部分
    val highCurrentTime = lastTimestampWrite[tagType] shr 24
    raf.write(highCurrentTime)
    return true
  }

  /**
   * @param raf
   * @param byteLength
   * @return
   * @throws IOException
   */
  private fun readBytesToInt(raf: InputStream, byteLength: Int): Int {
    raf.read(buffer, 0, byteLength)
    return bytes2Int(buffer, byteLength)
  }

  private fun int2Bytes(value: Int): ByteArray {
    val byteRet = ByteArray(4)
    for (i in 0..3) {
      byteRet[3 - i] = (value shr 8 * i and 0xff).toByte()
    }
    return byteRet
  }

  private fun bytes2Int(bytes: ByteArray, byteLength: Int): Int {
    var result = 0
    for (i in 0 until byteLength) {
      result = result or (bytes[byteLength - 1 - i].toInt() and 0xff shl i * 8)
      // System.out.printf("%x ",(bytes[i] & 0xff));
    }
    return result
  }
}
