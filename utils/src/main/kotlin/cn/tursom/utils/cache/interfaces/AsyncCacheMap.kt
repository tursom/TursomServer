package cn.tursom.utils.cache.interfaces

import cn.tursom.core.datastruct.async.interfaces.AsyncMap

interface AsyncCacheMap<K, V> : AsyncMap<K, V> {
	suspend fun get(key: K, constructor: suspend () -> V): V
}
