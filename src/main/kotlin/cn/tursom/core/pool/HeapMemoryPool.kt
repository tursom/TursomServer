package cn.tursom.core.pool

import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.core.bytebuffer.NioAdvanceByteBuffer
import cn.tursom.core.datastruct.ArrayBitSet
import java.nio.ByteBuffer

@Suppress("MemberVisibilityCanBePrivate")
class HeapMemoryPool(val blockSize: Int = 1024, val blockCount: Int = 16) : MemoryPool {
	private val memoryPool = ByteBuffer.allocate(blockSize * blockCount)
	private val bitMap = ArrayBitSet(blockCount.toLong())

	/**
	 * @return token
	 */
	override fun allocate(): Int = synchronized(this) {
		val index = bitMap.firstDown()
		if (index in 0 until blockCount) {
			bitMap.up(index)
			index.toInt()
		} else {
			-1
		}
	}

	override fun free(token: Int) {
		if (token in 0 until blockCount) synchronized(this) {
			bitMap.down(token.toLong())
		}
	}

	override fun getMemory(token: Int): ByteBuffer? = if (token in 0 until blockCount) {
		synchronized(this) {
			memoryPool.limit((token + 1) * blockSize)
			memoryPool.position(token * blockSize)
			return memoryPool.slice()
		}
	} else {
		null
	}

	override fun getAdvanceByteBuffer(token: Int): AdvanceByteBuffer? = if (token in 0 until blockCount) {
		synchronized(this) {
			memoryPool.limit((token + 1) * blockSize)
			memoryPool.position(token * blockSize)
			return NioAdvanceByteBuffer(memoryPool.slice())
		}
	} else {
		null
	}
}