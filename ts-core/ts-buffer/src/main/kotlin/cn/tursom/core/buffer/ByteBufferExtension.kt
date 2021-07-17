/**
 * 这个文件包含了一些可能出错的操作的封装
 * 还有一些是以内联形式封装以提高运行效率
 */

package cn.tursom.core.buffer

import cn.tursom.core.buffer.NioBuffers.Arrays.Companion.readArrays
import cn.tursom.core.buffer.NioBuffers.Arrays.Companion.writeArrays
import cn.tursom.core.buffer.NioBuffers.Lists.Companion.readLists
import cn.tursom.core.buffer.NioBuffers.Lists.Companion.writeLists
import cn.tursom.core.buffer.NioBuffers.Sequences.Companion.readSequences
import cn.tursom.core.buffer.NioBuffers.Sequences.Companion.writeSequences
import cn.tursom.core.buffer.NioBuffers.finishRead
import cn.tursom.core.buffer.NioBuffers.finishWrite
import cn.tursom.core.buffer.NioBuffers.getReadNioBufferList
import cn.tursom.core.buffer.NioBuffers.getWriteNioBufferList
import cn.tursom.core.buffer.impl.ArrayByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.buffer.impl.ListByteBuffer
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

//inline fun <T> MultipleByteBuffer.reads(block: (Sequence<java.nio.ByteBuffer>) -> T): T {
//  val bufferList = readBufferSequence()
//  try {
//    return block(bufferList)
//  } finally {
//    finishRead(bufferList)
//  }
//}

//inline fun <T> MultipleByteBuffer.writes(block: (Sequence<java.nio.ByteBuffer>) -> T): T {
//  val bufferList = writeBufferSequence()
//  try {
//    return block(bufferList)
//  } finally {
//    finishWrite(bufferList)
//  }
//}

fun ReadableByteChannel.read(buffer: ByteBuffer): Int {
  if (this is ScatteringByteChannel) {
    val arrays = buffer.getExtension(NioBuffers.Arrays)
    if (arrays != null) {
      return arrays.writeArrays { nioBuffers ->
        read(nioBuffers)
      }.toInt()
    }

    val list = buffer.getExtension(NioBuffers.Lists)
    if (list != null) {
      return list.writeLists { nioBuffers ->
        read(nioBuffers.toTypedArray())
      }.toInt()
    }

    val sequences = buffer.getExtension(NioBuffers.Sequences)
    if (sequences != null) {
      return sequences.writeSequences { nioBuffers ->
        read(nioBuffers.toList().toTypedArray())
      }.toInt()
    }
  }

  return buffer.write { read(it) }
}

fun WritableByteChannel.write(buffer: ByteBuffer): Int {
  if (this is GatheringByteChannel) {
    val arrays = buffer.getExtension(NioBuffers.Arrays)
    if (arrays != null) {
      return arrays.readArrays { nioBuffers ->
        write(nioBuffers)
      }.toInt()
    }

    val list = buffer.getExtension(NioBuffers.Lists)
    if (list != null) {
      return list.readLists { nioBuffers ->
        write(nioBuffers.toTypedArray())
      }.toInt()
    }

    val sequences = buffer.getExtension(NioBuffers.Sequences)
    if (sequences != null) {
      return sequences.readSequences { nioBuffers ->
        write(nioBuffers.toList().toTypedArray())
      }.toInt()
    }
  }
  return buffer.read { write(it) }
}

//fun ScatteringByteChannel.read(buffer: MultipleByteBuffer): Long {
//  return buffer.writeBuffers { read(it.toList().toTypedArray()) }
//}

//fun GatheringByteChannel.write(buffer: MultipleByteBuffer): Long {
//  return buffer.readBuffers { write(it.toList().toTypedArray()) }
//}

fun ScatteringByteChannel.read(buffers: Array<out ByteBuffer>): Long {
  val bufferList = buffers.iterator().getWriteNioBufferList()
  val bufferArray = bufferList.toTypedArray()
  return try {
    read(bufferArray)
  } finally {
    val iterator = bufferList.iterator()
    buffers.iterator().finishWrite(iterator)
  }
}

fun GatheringByteChannel.write(buffers: Array<out ByteBuffer>): Long {
  val bufferList = buffers.iterator().getReadNioBufferList()
  val bufferArray = bufferList.toTypedArray()
  return try {
    write(bufferArray)
  } finally {
    val iterator = bufferList.iterator()
    buffers.iterator().finishRead(iterator)
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

fun main() {
  println(HeapByteBuffer(1).getExtension(NioBuffers.Sequences))
  println(ListByteBuffer().getExtension(NioBuffers.Sequences))
}
