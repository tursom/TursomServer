package cn.tursom.core.datastruct.async.interfaces

interface AsyncPotableSet<K> : AsyncSet<K> {
	suspend fun clear(): AsyncPotableSet<K>
	suspend fun put(key: K): AsyncPotableSet<K>
	suspend fun putIfAbsent(key: K): Boolean
	suspend infix fun putAll(from: Set<K>): AsyncPotableSet<K>
	suspend infix fun remove(key: K): AsyncPotableSet<K>
}