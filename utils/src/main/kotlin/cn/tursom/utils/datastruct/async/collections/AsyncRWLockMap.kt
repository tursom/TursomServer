package cn.tursom.utils.datastruct.async.collections

import cn.tursom.utils.asynclock.AsyncRWLock

open class AsyncRWLockMap<K, V>(
	override val lock: AsyncRWLock,
	map: Map<K, V>
) : AsyncLockMap<K, V>(lock, map) {
	override suspend fun isEmpty(): Boolean {
		return lock.doRead { map.isEmpty() }
	}

	override suspend fun isNotEmpty(): Boolean {
		return lock.doRead { map.isNotEmpty() }
	}

	override suspend fun get(key: K): V? {
		return lock.doRead { map[key] }
	}

	override suspend fun containsKey(key: K): Boolean {
		return lock.doRead { map.containsKey(key) }
	}

	override suspend fun containsValue(value: V): Boolean {
		return lock.doRead { map.containsValue(value) }
	}

	override suspend fun forEach(action: suspend (Map.Entry<K, V>) -> Boolean): Boolean {
		return lock.doRead {
			map.forEach {
				if (!action(it)) return@doRead false
			}
			true
		}
	}
}