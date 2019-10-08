package cn.tursom.utils.pool

import cn.tursom.core.datastruct.ArrayBitSet
import cn.tursom.core.pool.AsyncPool
import cn.tursom.utils.asynclock.AsyncLock
import cn.tursom.utils.asynclock.AsyncMutexLock

class AsyncArrayPool<T>(private val lock: AsyncLock = AsyncMutexLock()) : AsyncPool<T> {
	private val bitSet = ArrayBitSet(64)
	private var poll = Array<Any?>(bitSet.size.toInt()) { null }


	override suspend fun put(cache: T): Boolean {
		val index = lock {
			val index = getDown()
			bitSet.up(index)
			index
		}.toInt()
		poll[index] = cache
		return true
	}

	override suspend fun get(): T? {
		return lock {
			val index = bitSet.firstUp()
			if (index < 0) null
			else {
				@Suppress("UNCHECKED_CAST")
				val ret = poll[index.toInt()] as T
				bitSet.down(index)
				ret
			}
		}
	}

	private fun getDown(): Long {
		val index = bitSet.firstDown()
		return if (index < 0) {
			resize()
			bitSet.firstDown()
		} else {
			index
		}
	}

	private fun resize() {
		bitSet.resize(bitSet.size * 2)
	}
}