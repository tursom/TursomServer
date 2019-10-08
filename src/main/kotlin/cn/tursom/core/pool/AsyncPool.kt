package cn.tursom.core.pool

interface AsyncPool<T> {
	suspend fun put(cache: T): Boolean
	suspend fun get(): T?
	
	suspend fun <T> AsyncPool<T>.forceGet(): T {
		return get() ?: throw Pool.NoCacheException()
	}
}