package cn.tursom.core.encrypt

import cn.tursom.core.buffer.ByteBuffer

interface Encrypt {
  var encryptInitVector: ByteArray?
    get() = null
    set(_) {}
  var decryptInitVector: ByteArray?
    get() = null
    set(_) {}

  fun encrypt(data: ByteArray, offset: Int = 0, size: Int = data.size - offset): ByteArray
  fun decrypt(data: ByteArray, offset: Int = 0, size: Int = data.size - offset): ByteArray
  fun encrypt(
    data: ByteArray,
    buffer: ByteArray,
    bufferOffset: Int = 0,
    offset: Int = 0,
    size: Int = data.size - offset
  ): Int

  fun decrypt(
    data: ByteArray,
    buffer: ByteArray,
    bufferOffset: Int = 0,
    offset: Int = 0,
    size: Int = data.size - offset
  ): Int

  fun encrypt(data: ByteBuffer): ByteArray {
    return if (data.hasArray) {
      encrypt(data.array, data.readOffset, data.writeOffset)
    } else {
      encrypt(data.getBytes())
    }
  }

  fun decrypt(data: ByteBuffer): ByteArray {
    return if (data.hasArray) {
      decrypt(data.array, data.readOffset, data.writeOffset)
    } else {
      decrypt(data.getBytes())
    }
  }
}