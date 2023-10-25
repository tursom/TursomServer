/**
 * 这个文件包含了一些可能出错的操作的封装
 * 还有一些是以内联形式封装以提高运行效率
 */

package cn.tursom.core.buffer

import cn.tursom.core.buffer.impl.ArrayByteBuffer
import cn.tursom.core.util.toBytes
import cn.tursom.core.util.toInt
import java.nio.ByteOrder
import java.nio.channels.GatheringByteChannel
import java.nio.channels.ReadableByteChannel
import java.nio.channels.ScatteringByteChannel
import java.nio.channels.WritableByteChannel


/**
 * 使用读 buffer，ByteBuffer 实现类有义务维护指针正常推进
 */
inline fun <T> ReadableByteBuffer.read(block: (java.nio.ByteBuffer) -> T): T {
  val buffer = readBuffer()
  return try {
    block(buffer)
  } finally {
    finishRead(buffer)
  }
}

/**
 * 使用写 buffer，ByteBuffer 实现类有义务维护指针正常推进
 */
inline fun <T> WriteableByteBuffer.write(block: (java.nio.ByteBuffer) -> T): T {
  val buffer = writeBuffer()
  return try {
    block(buffer)
  } finally {
    finishWrite(buffer)
  }
}

fun ReadableByteChannel.read(buffer: ByteBuffer): Long {
  if (buffer is NioBuffers.Arrays) {
    val buffers = buffer.writeBufferArray()
    try {
      if (this is ScatteringByteChannel) {
        return read(buffers)
      }

      var read = 0L
      buffers.forEach { buf ->
        if (buf.remaining() == 0) {
          return@forEach
        }
        val readOnce = read(buf)
        if (readOnce == 0) {
          return read
        }
        read += readOnce
      }
      return read
    } finally {
      buffer.finishWrite(buffers)
    }
  }

  return buffer.write { read(it).toLong() }
}

fun WritableByteChannel.write(buffer: ByteBuffer): Long {
  if (buffer is NioBuffers.Arrays) {
    val buffers = buffer.readBufferArray()
    try {
      if (this is GatheringByteChannel) {
        return write(buffers)
      }

      var write = 0L
      buffers.forEach { buf ->
        if (buf.remaining() == 0) {
          return@forEach
        }
        val writeOnce = write(buf)
        if (writeOnce == 0) {
          return write
        }
        write += writeOnce
      }
      return write
    } finally {
      buffer.finishRead(buffers)
    }
  }

  return buffer.read { write(it).toLong() }
}

fun Array<out ByteBuffer>.asMultipleByteBuffer() = ArrayByteBuffer(buffers = this)

val Collection<ByteBuffer>.readable: Int
  get() {
    var size = 0
    forEach { size += it.readable }
    return size
  }

val Collection<ByteBuffer>.writeable: Int
  get() {
    var size = 0
    forEach { size += it.writeable }
    return size
  }

fun ByteBuffer.getIntWithSize(size: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
  var time = 4
  return toInt(byteOrder) {
    if (--time < size) {
      get()
    } else {
      0
    }
  }
}

fun ByteBuffer.getLongWithSize(size: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
  var time = 8
  return toInt(byteOrder) {
    if (--time < size) {
      get()
    } else {
      0
    }
  }
}

fun ByteBuffer.putIntWithSize(n: Int, size: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN) {
  when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      var time = size
      n.toBytes(ByteOrder.LITTLE_ENDIAN) {
        if (time++ < 4) {
          put(it)
        }
      }
    }
    ByteOrder.BIG_ENDIAN -> {
      var time = size
      n.toBytes(ByteOrder.BIG_ENDIAN) {
        if (++time > 4) {
          put(it)
        }
      }
    }
  }
}

fun ByteBuffer.putLongWithSize(l: Long, size: Int, byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN) {
  when (byteOrder) {
    ByteOrder.LITTLE_ENDIAN -> {
      var time = size
      l.toBytes(ByteOrder.LITTLE_ENDIAN) {
        if (time++ < 8) {
          put(it)
        }
      }
    }
    ByteOrder.BIG_ENDIAN -> {
      var time = size
      l.toBytes(ByteOrder.BIG_ENDIAN) {
        if (++time > 8) {
          put(it)
        }
      }
    }
  }
}
