package cn.tursom.core.buffer.impl

import cn.tursom.buffer.MultipleByteBuffer
import cn.tursom.core.buffer.ByteBuffer

open class ListByteBuffer(val bufferList: List<ByteBuffer>) : MultipleByteBuffer, List<ByteBuffer> by bufferList {
  private var readOperator = bufferList.firstOrNull()
  private var writeOperator = bufferList.firstOrNull()
  override val hasArray: Boolean get() = false
  override val array: ByteArray
    get() = throw UnsupportedOperationException()
  override val capacity: Int
    get() {
      var capacity = 0
      bufferList.forEach {
        capacity += it.capacity
      }
      return capacity
    }
  override val arrayOffset: Int get() = 0
  override var writePosition: Int = 0
  override var readPosition: Int = 0
  override val resized: Boolean get() = false

  override fun readBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()
  override fun writeBuffer(): java.nio.ByteBuffer = throw UnsupportedOperationException()
  override fun slice(position: Int, size: Int, readPosition: Int, writePosition: Int): ByteBuffer =
    throw UnsupportedOperationException()

  override fun resize(newSize: Int): Boolean = throw UnsupportedOperationException()

  fun updateRead() {
    if (readOperator == null || readOperator!!.readable == 0) {
      readOperator = bufferList[readPosition++]
    }
  }

  fun updateWrite() {
    if (writeOperator == null || writeOperator!!.readable == 0) {
      writeOperator = bufferList[writePosition++]
    }
  }

  override fun get(): Byte {
    updateRead()
    return readOperator!!.get()
  }
  override fun put(byte: Byte) {
    TODO("Not yet implemented")
  }

  override fun split(maxSize: Int): Array<out ByteBuffer> {
    TODO("Not yet implemented")
  }

  override fun readAllSize(): Int {
    TODO("Not yet implemented")
  }
}