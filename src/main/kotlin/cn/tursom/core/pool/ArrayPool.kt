package cn.tursom.core.pool

import cn.tursom.core.datastruct.ArrayBitSet

class ArrayPool<T> : Pool<T> {
	private val bitSet = ArrayBitSet(64)
	private var poll = Array<Any?>(bitSet.size.toInt()) { null }

	override fun put(cache: T): Boolean {
		val index = synchronized(bitSet) {
			val index = getDown()
			bitSet.up(index)
			index
		}.toInt()
		poll[index] = cache
		return true
	}

	override fun get(): T? = synchronized(bitSet) {
		val index = bitSet.firstUp()
		if (index < 0) null
		else {
			@Suppress("UNCHECKED_CAST")
			val ret = poll[index.toInt()] as T
			bitSet.down(index)
			ret
		}
	}

	private fun getDown(): Long = synchronized(bitSet) {
		val index = bitSet.firstDown()
		if (index < 0) {
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