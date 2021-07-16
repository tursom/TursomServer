/**
 * 这个文件包含了一些可能出错的操作的封装
 * 还有一些是以内联形式封装以提高运行效率
 */

package cn.tursom.core.buffer

import cn.tursom.core.buffer.impl.ArrayByteBuffer
import cn.tursom.core.toBytes
import cn.tursom.core.toInt
import java.nio.ByteOrder
import java.nio.channels.GatheringByteChannel
import java.nio.channels.ReadableByteChannel
import java.nio.channels.ScatteringByteChannel
import java.nio.channels.WritableByteChannel


/**
 * 使用读 buffer，ByteBuffer 实现类有义务维护指针正常推进
 */
inline fun <T> ByteBuffer.read(block: (java.nio.ByteBuffer) -> T): T {
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
inline fun <T> ByteBuffer.write(block: (java.nio.ByteBuffer) -> T): T {
  val buffer = writeBuffer()
  return try {
    block(buffer)
  } finally {
    finishWrite(buffer)
  }
}

inline fun <T> MultipleByteBuffer.reads(block: (Sequence<java.nio.ByteBuffer>) -> T): T {
  val bufferList = readBuffers()
  try {
    return block(bufferList)
  } finally {
    finishRead(bufferList)
  }
}


inline fun <T> MultipleByteBuffer.writes(block: (Sequence<java.nio.ByteBuffer>) -> T): T {
  val bufferList = writeBuffers()
  try {
    return block(bufferList)
  } finally {
    finishWrite(bufferList)
  }
}

fun ReadableByteChannel.read(buffer: ByteBuffer): Int {
  return if (buffer is MultipleByteBuffer && this is ScatteringByteChannel) {
    buffer.writeBuffers { read(it.toList().toTypedArray()) }.toInt()
  } else {
    buffer.write { read(it) }
  }
}

fun WritableByteChannel.write(buffer: ByteBuffer): Int {
  return if (buffer is MultipleByteBuffer && this is GatheringByteChannel) {
    buffer.readBuffers { write(it.toList().toTypedArray()) }.toInt()
  } else {
    buffer.read { write(it) }
  }
}

fun ScatteringByteChannel.read(buffer: MultipleByteBuffer): Long {
  return buffer.writeBuffers { read(it.toList().toTypedArray()) }
}

fun GatheringByteChannel.write(buffer: MultipleByteBuffer): Long {
  return buffer.readBuffers { write(it.toList().toTypedArray()) }
}

fun ScatteringByteChannel.read(buffers: Array<out ByteBuffer>): Long {
  val bufferList = ArrayList<java.nio.ByteBuffer>()
  buffers.forEach {
    if (it is MultipleByteBuffer) {
      it.writeBuffers().forEach { nioBuffer ->
        bufferList.add(nioBuffer)
      }
    } else {
      bufferList.add(it.writeBuffer())
    }
  }
  val bufferArray = bufferList.toTypedArray()
  return try {
    read(bufferArray)
  } finally {
    var index = 0
    val nioBuffers = bufferList.iterator()
    buffers.forEach {
      if (it is MultipleByteBuffer) {
        it.finishWrite(nioBuffers)
      } else {
        it.finishWrite(nioBuffers.next())
      }
    }
    index++
  }
}

fun GatheringByteChannel.write(buffers: Array<out ByteBuffer>): Long {
  val bufferList = ArrayList<java.nio.ByteBuffer>()
  buffers.forEach {
    if (it is MultipleByteBuffer) {
      it.readBuffers().forEach { nioBuffer ->
        bufferList.add(nioBuffer)
      }
    } else {
      bufferList.add(it.readBuffer())
    }
  }
  val bufferArray = bufferList.toTypedArray()
  return try {
    write(bufferArray)
  } finally {
    var index = 0
    val iterator = bufferList.iterator()
    buffers.forEach {
      if (it is MultipleByteBuffer) {
        it.finishRead(iterator)
      } else {
        it.finishRead(iterator.next())
      }
    }
    index++
  }
}

fun Array<out ByteBuffer>.asMultipleByteBuffer() = ArrayByteBuffer(*this)

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

fun ByteBuffer.getIntWithSize(size: Int, byteOrder: ByteOrder = ByteOrder.nativeOrder()): Int {
  var time = 4
  return toInt(byteOrder) {
    if (--time < size) {
      get()
    } else {
      0
    }
  }
}

fun ByteBuffer.getLongWithSize(size: Int, byteOrder: ByteOrder = ByteOrder.nativeOrder()): Int {
  var time = 8
  return toInt(byteOrder) {
    if (--time < size) {
      get()
    } else {
      0
    }
  }
}

fun ByteBuffer.putIntWithSize(n: Int, size: Int, byteOrder: ByteOrder = ByteOrder.nativeOrder()) {
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

fun ByteBuffer.putLongWithSize(l: Long, size: Int, byteOrder: ByteOrder = ByteOrder.nativeOrder()) {
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

