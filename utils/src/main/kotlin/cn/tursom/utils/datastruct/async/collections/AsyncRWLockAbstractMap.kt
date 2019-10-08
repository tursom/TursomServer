package cn.tursom.utils.datastruct.async.collections

import cn.tursom.utils.asynclock.AsyncRWLock
import cn.tursom.core.datastruct.async.interfaces.AsyncPotableMap

class AsyncRWLockAbstractMap<K, V>(
	lock: AsyncRWLock,
	override val map: java.util.AbstractMap<K, V> = HashMap()
) : AsyncRWLockMap<K, V>(lock, map), AsyncPotableMap<K, V> {
	constructor(lock: AsyncRWLock) : this(lock, HashMap())

	override suspend fun set(key: K, value: V): V? {
		return lock.doWrite {
			map.put(key, value)
		}
	}

	override suspend fun remove(key: K): V? {
		return lock.doWrite { map.remove(key) }
	}

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
			from.forEach { (k, u) ->
				map[k] = u
			}
		}
	}

	override fun toString(): String = map.toString()
}
