package cn.tursom.utils.datastruct.async.collections

import cn.tursom.utils.asynclock.AsyncLock
import cn.tursom.utils.asynclock.AsyncRWLock
import cn.tursom.core.datastruct.async.interfaces.AsyncPotableMap

class AsyncLockAbstractMap<K, V>(
	override val lock: AsyncLock,
	override val map: java.util.AbstractMap<K, V> = HashMap()
) : AsyncLockMap<K, V>(lock, map), AsyncPotableMap<K, V> {
	override suspend fun clear() {
		lock { map.clear() }
	}

	override suspend fun putIfAbsent(key: K, value: V): Boolean {
		return lock {
			if (map.containsKey(key)) false
			else {
				map[key] = value
				true
			}
		}
	}

	override suspend fun putAll(from: Map<out K, V>) {
		lock {
			map.putAll(from)
		}
	}

	constructor(lock: AsyncRWLock) : this(lock, HashMap())

	override suspend fun set(key: K, value: V): V? {
		return lock {
			map.put(key, value)
		}
	}

	override suspend fun remove(key: K): V? {
		return lock { map.remove(key) }
	}

	override fun toString(): String = map.toString()
}