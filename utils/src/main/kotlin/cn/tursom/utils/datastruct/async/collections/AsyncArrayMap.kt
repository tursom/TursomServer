package cn.tursom.utils.datastruct.async.collections

import cn.tursom.utils.asynclock.AsyncWriteFirstRWLock
import cn.tursom.core.datastruct.ArrayMap
import cn.tursom.core.datastruct.async.interfaces.AsyncCollection
import cn.tursom.core.datastruct.async.interfaces.AsyncPotableMap
import cn.tursom.core.datastruct.async.interfaces.AsyncSet

class AsyncArrayMap<K : Comparable<K>, V> : AsyncPotableMap<K, V> {
	private val lock = AsyncWriteFirstRWLock()
	private val map = ArrayMap<K, V>()

	override val size: Int get() = map.size
	override val entries: AsyncSet<Map.Entry<K, V>> = AsyncEntrySet(this)
	override val keys: AsyncSet<K> = AsyncKeySet(this)
	override val values: AsyncCollection<V> = AsyncValueCollection(this)

	override suspend fun containsKey(key: K): Boolean {
		return lock.doRead { map.containsKey(key) }
	}

	override suspend fun containsValue(value: V): Boolean {
		return lock.doRead { map.containsValue(value) }
	}

	override suspend fun get(key: K): V? {
		return lock.doRead { map[key] }
	}

	override suspend fun isEmpty(): Boolean {
		return lock.doRead { map.isEmpty() }
	}

	override suspend fun clear() {
		lock.doWrite { map.clear() }
	}

	override suspend fun set(key: K, value: V): V? {
		return lock { map.setAndGet(key, value) }
	}

	override suspend fun putIfAbsent(key: K, value: V): Boolean {
		return lock {
			if (containsKey(key)) false
			else {
				map.setAndGet(key, value)
				true
			}
		}
	}

	override suspend fun putAll(from: Map<out K, V>) {
		return lock { map.putAll(from) }
	}

	override suspend fun remove(key: K): V? {
		return lock { map.delete(key) }
	}

	override suspend fun contains(element: Map.Entry<K, V>): Boolean {
		return get(element.key) == element.value
	}

	override suspend fun containsAll(elements: AsyncCollection<Map.Entry<K, V>>): Boolean {
		return elements.forEach { contains(it) }
	}

	override suspend fun forEach(action: suspend (Map.Entry<K, V>) -> Boolean): Boolean {
		return lock {
			map.forEach { !action(it) }
			true
		}
	}


	class AsyncEntrySet<K : Comparable<K>, V>(private val map: AsyncArrayMap<K, V>) : AsyncSet<Map.Entry<K, V>> {
		override val size: Int
			get() = map.size

		override suspend fun contains(element: Map.Entry<K, V>): Boolean {
			return map.get(element.key) == element.value
		}

		override suspend fun containsAll(elements: AsyncCollection<Map.Entry<K, V>>): Boolean {
			return elements.forEach { contains(it) }
		}

		override suspend fun isEmpty(): Boolean {
			return map.isEmpty()
		}

		override suspend fun forEach(action: suspend (Map.Entry<K, V>) -> Boolean): Boolean {
			return map.forEach(action)
		}
	}

	class AsyncKeySet<K : Comparable<K>>(private val map: AsyncArrayMap<K, *>) : AsyncSet<K> {
		override val size: Int
			get() = map.size

		override suspend fun contains(element: K): Boolean {
			return map.containsKey(element)
		}

		override suspend fun containsAll(elements: AsyncCollection<K>): Boolean {
			return elements.forEach { map.containsKey(it) }
		}

		override suspend fun isEmpty(): Boolean {
			return size == 0
		}

		override suspend fun forEach(action: suspend (K) -> Boolean): Boolean {
			return map.forEach { action(it.key) }
		}
	}

	class AsyncValueCollection<V>(private val map: AsyncArrayMap<*, V>) : AsyncCollection<V> {
		override val size: Int
			get() = map.size

		override suspend fun contains(element: V): Boolean {
			return map.containsValue(element)
		}

		override suspend fun containsAll(elements: AsyncCollection<V>): Boolean {
			return elements.forEach { !map.containsValue(it) }
		}

		override suspend fun isEmpty(): Boolean {
			return size == 0
		}

		override suspend fun forEach(action: suspend (V) -> Boolean): Boolean {
			return map.forEach { action(it.value) }
		}
	}
}