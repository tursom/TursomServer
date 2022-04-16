package cn.tursom.core.buffer

interface BasicByteBuffer {
  val hasArray: Boolean
  val array: ByteArray

  val readable: Int get() = writePosition - readPosition
  val writeable: Int get() = capacity - writePosition

  val isReadable: Boolean get() = readable != 0
  val isWriteable: Boolean get() = writeable != 0

  val capacity: Int
  val arrayOffset: Int
  var writePosition: Int
  var readPosition: Int
  val writeOffset: Int get() = arrayOffset + writePosition
  val readOffset: Int get() = arrayOffset + readPosition

  val closed: Boolean get() = false
  val resized: Boolean

  fun reset()

  fun clear() {
    readPosition = 0
    writePosition = 0
  }
}
