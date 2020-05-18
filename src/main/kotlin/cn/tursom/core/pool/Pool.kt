package cn.tursom.core.pool

interface Pool<T> {
  fun put(cache: T): Boolean
  fun get(): T?

  class NoCacheException : Exception()

  fun forceGet(): T {
    return get() ?: throw NoCacheException()
  }
}