package cn.tursom.core.datastruct.async.interfaces

interface AsyncPotableMap<K, V> : AsyncMap<K, V> {
  suspend fun clear()
  suspend fun set(key: K, value: V): V?
  suspend fun putIfAbsent(key: K, value: V): Boolean
  suspend infix fun putAll(from: Map<out K, V>)
  suspend infix fun remove(key: K): V?
}