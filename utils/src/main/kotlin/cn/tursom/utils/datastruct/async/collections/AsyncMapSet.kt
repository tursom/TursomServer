package cn.tursom.utils.datastruct.async.collections

import cn.tursom.utils.asynclock.AsyncWriteFirstRWLock
import cn.tursom.core.datastruct.SetMap
import cn.tursom.core.datastruct.async.interfaces.AsyncCollection
import cn.tursom.core.datastruct.async.interfaces.AsyncPotableMap
import cn.tursom.core.datastruct.async.interfaces.AsyncPotableSet

open class AsyncMapSet<K>(private val map: AsyncPotableMap<K, Unit> = AsyncRWLockAbstractMap(AsyncWriteFirstRWLock())) : AsyncPotableSet<K> {
	override val size: Int get() = map.size

	override suspend fun isEmpty(): Boolean {
		return size == 0
	}

	override suspend fun contains(element: K): Boolean {
		return map.containsKey(element)
	}

	override suspend fun containsAll(elements: AsyncCollection<K>): Boolean {
		return elements.forEach { map.containsKey(it) }
	}

	override suspend fun clear(): AsyncPotableSet<K> {
		map.clear()
		return this
	}

	override suspend fun put(key: K): AsyncPotableSet<K> {
		map.set(key, Unit)
		return this
	}

	override suspend fun putIfAbsent(key: K): Boolean {
		return map.putIfAbsent(key, Unit)
	}

	override suspend fun putAll(from: Set<K>): AsyncPotableSet<K> {
		map.putAll(SetMap(from))
		return this
	}

	override suspend fun remove(key: K): AsyncPotableSet<K> {
		map.remove(key)
		return this
	}

	override suspend fun forEach(action: suspend (K) -> Boolean): Boolean {
		return map.forEach { action(it.key) }
	}
}

