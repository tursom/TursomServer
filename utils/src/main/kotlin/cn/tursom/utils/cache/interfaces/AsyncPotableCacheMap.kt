package cn.tursom.utils.cache.interfaces

import cn.tursom.core.datastruct.async.interfaces.AsyncPotableMap

interface AsyncPotableCacheMap<K, V> : AsyncCacheMap<K, V>, AsyncPotableMap<K, V>