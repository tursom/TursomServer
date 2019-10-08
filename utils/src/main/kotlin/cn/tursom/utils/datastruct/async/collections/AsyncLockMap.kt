package cn.tursom.utils.datastruct.async.collections

import cn.tursom.utils.asynclock.AsyncLock
import cn.tursom.core.datastruct.async.interfaces.AsyncCollection
import cn.tursom.core.datastruct.async.interfaces.AsyncMap
import cn.tursom.core.datastruct.async.interfaces.AsyncSet

open class AsyncLockMap<K, V>(
	protected open val lock: AsyncLock,
	protected open val map: Map<K, V>
) : AsyncMap<K, V> {
	override val size: Int get() = map.size
	override val entries: AsyncSet<Map.Entry<K, V>> = Entries()
	override val keys: AsyncSet<K> = Keys()
	override val values: AsyncCollection<V> = Values()

	override suspend fun containsKey(key: K): Boolean {
		return lock { map.containsKey(key) }
	}

	override suspend fun containsValue(value: V): Boolean {
		return lock { map.containsValue(value) }
	}

	override suspend fun isEmpty(): Boolean {
		return lock { map.isEmpty() }
	}

	override suspend fun get(key: K): V? {
		return lock { map[key] }
	}

	override suspend fun forEach(action: suspend (Map.Entry<K, V>) -> Boolean): Boolean {
		return lock {
			map.forEach {
				if (!action(it)) return@lock false
			}
			true
		}
	}

	override fun toString(): String = map.toString()

	inner class Entries : AsyncSet<Map.Entry<K, V>> {
		override val size: Int get() = this@AsyncLockMap.size
		override suspend fun isEmpty(): Boolean {
			return this@AsyncLockMap.isEmpty()
		}

		override suspend fun contains(element: Map.Entry<K, V>): Boolean {
			return contains(element)
		}

		override suspend fun containsAll(elements: AsyncCollection<Map.Entry<K, V>>): Boolean {
			return containsAll(elements)
		}

		override suspend fun forEach(action: suspend (Map.Entry<K, V>) -> Boolean): Boolean {
			return this@AsyncLockMap.forEach { action(it) }
		}
	}

	inner class Keys : AsyncSet<K> {
		override val size: Int
			get() = this@AsyncLockMap.size

		override suspend fun isEmpty(): Boolean {
			return this@AsyncLockMap.isEmpty()
		}

		override suspend fun contains(element: K): Boolean {
			return containsKey(element)
		}

		override suspend fun containsAll(elements: AsyncCollection<K>): Boolean {
			return elements.forEach { containsKey(it) }
		}

		override suspend fun forEach(action: suspend (K) -> Boolean): Boolean {
			return this@AsyncLockMap.forEach { action(it.key) }
		}
	}

	inner class Values : AsyncCollection<V> {
		override val size: Int
			get() = this@AsyncLockMap.size

		override suspend fun isEmpty(): Boolean {
			return this@AsyncLockMap.isEmpty()
		}

		override suspend fun contains(element: V): Boolean {
			return containsValue(element)
		}

		override suspend fun containsAll(elements: AsyncCollection<V>): Boolean {
			return elements.forEach { containsValue(it) }
		}

		override suspend fun forEach(action: suspend (V) -> Boolean): Boolean {
			return this@AsyncLockMap.forEach { action(it.value) }
		}

	}
}