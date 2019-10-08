package cn.tursom.core.encrypt

interface Encrypt {
	fun encrypt(data: ByteArray, offset: Int = 0, size: Int = data.size - offset): ByteArray
	fun decrypt(data: ByteArray, offset: Int = 0, size: Int = data.size - offset): ByteArray
	fun encrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int = 0, offset: Int = 0, size: Int = data.size - offset): Int
	fun decrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int = 0, offset: Int = 0, size: Int = data.size - offset): Int
}