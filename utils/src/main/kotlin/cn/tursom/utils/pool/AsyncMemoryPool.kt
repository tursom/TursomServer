package cn.tursom.utils.pool

import cn.tursom.core.bytebuffer.HeapByteBuffer
import cn.tursom.core.datastruct.ArrayBitSet
import cn.tursom.core.pool.AsyncPool
import cn.tursom.utils.asynclock.AsyncMutexLock
import java.nio.ByteBuffer

@Suppress("MemberVisibilityCanBePrivate")
class AsyncMemoryPool(val blockSize: Int = 1024, val blockCount: Int = 16) : AsyncPool<ByteBuffer> {
	private val memoryPool = ByteArray(blockSize * blockCount)
	private val bitSet = ArrayBitSet(blockCount.toLong())
	private val lock = AsyncMutexLock()

	override suspend fun put(cache: ByteBuffer): Boolean {
		if (cache.array() !== memoryPool) return false

		val index = (cache.arrayOffset() / blockSize).toLong()
		lock {
			bitSet.down(index)
		}

		cache.asCharBuffer()
		return true
	}

	override suspend fun get(): ByteBuffer? {
		val index = lock {
			val index = bitSet.firstDown()
			if (index >= 0) bitSet.up(index)
			index
		}.toInt()
		return if (index < 0 || index > blockCount) null
		else HeapByteBuffer.wrap(memoryPool, blockSize * index, blockSize)
	}

	fun contain(buffer: ByteBuffer) = buffer.array() === memoryPool
}


