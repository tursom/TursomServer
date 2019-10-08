package cn.tursom.utils.cache

import cn.tursom.utils.cache.interfaces.AsyncPotableCacheMap
import cn.tursom.utils.datastruct.async.ReadWriteLockHashMap
import cn.tursom.core.datastruct.async.interfaces.AsyncCollection
import cn.tursom.core.datastruct.async.interfaces.AsyncPotableMap
import cn.tursom.core.datastruct.async.interfaces.AsyncSet

open class DefaultAsyncPotableCacheMap<K, V>(
	val timeout: Long,
	private val valueMap: AsyncPotableMap<K, Pair<Long, V>> = ReadWriteLockHashMap()
) : AsyncPotableCacheMap<K, V> {
	override val size: Int
		get() = valueMap.size
	override val entries: AsyncSet<Map.Entry<K, V>> = Entries()
	override val keys: AsyncSet<K> = valueMap.keys
	override val values: AsyncCollection<V> = Values()

	override suspend fun containsKey(key: K): Boolean = valueMap.containsKey(key)
	override suspend fun containsValue(value: V): Boolean = !valueMap.forEach { it.value != value }
	override suspend fun isEmpty(): Boolean = valueMap.isEmpty()

	override suspend fun get(key: K): V? {
		val value = getCache(key) ?: return null
		return value
	}

	override suspend fun get(key: K, constructor: suspend () -> V): V {
		val value = get(key) ?: run {
			val newValue = constructor()
			addCache(key, newValue)
			newValue
		}
		return value
	}

	override suspend fun set(key: K, value: V): V? {
		valueMap.set(key, System.currentTimeMillis() to value)
		return value
	}

	override suspend fun putIfAbsent(key: K, value: V): Boolean {
		return valueMap.putIfAbsent(key, System.currentTimeMillis() to value)
	}

	override suspend fun putAll(from: Map<out K, V>) {
		from.forEach { (k, v) ->
			valueMap.set(k, System.currentTimeMillis() to v)
		}
	}

	override suspend fun remove(key: K): V? = valueMap.remove(key)?.second

	override suspend fun clear() {
		valueMap.clear()
	}

	override suspend fun forEach(action: suspend (Map.Entry<K, V>) -> Boolean): Boolean {
		return valueMap.forEach { action(Entry(it.key, it.value.second)) }
	}

	private suspend fun getCache(key: K): V? {
		val cache = valueMap.get(key) ?: return null
		if (cache.first.isTimeOut()) {
			delCache(key)
			return null
		}
		return cache.second
	}

	private suspend fun delCache(key: K) {
		valueMap.remove(key)
	}

	private suspend fun addCache(key: K, value: V) {
		valueMap.set(key, System.currentTimeMillis() to value)
	}

	private fun Long.isTimeOut() = timeout != 0L && System.currentTimeMillis() - this > timeout

	data class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>

	inner class Entries : AsyncSet<Map.Entry<K, V>> {
		override val size: Int get() = valueMap.size

		override suspend fun isEmpty(): Boolean = valueMap.isEmpty()

		override suspend fun contains(element: Map.Entry<K, V>): Boolean = contains(element)

		override suspend fun containsAll(elements: AsyncCollection<Map.Entry<K, V>>): Boolean = containsAll(elements)

		override suspend fun forEach(action: suspend (Map.Entry<K, V>) -> Boolean): Boolean =
			this@DefaultAsyncPotableCacheMap.forEach(action)
	}

	inner class Values : AsyncCollection<V> {
		override val size: Int get() = valueMap.size
		override suspend fun isEmpty(): Boolean = valueMap.isEmpty()
		override suspend fun contains(element: V): Boolean = containsValue(element)

		override suspend fun containsAll(elements: AsyncCollection<V>): Boolean {
			return elements.forEach { contains(it) }
		}

		override suspend fun forEach(action: suspend (V) -> Boolean): Boolean {
			return valueMap.forEach { action(it.value.second) }
		}
	}
}