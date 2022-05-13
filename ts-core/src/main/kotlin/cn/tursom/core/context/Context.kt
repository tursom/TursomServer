package cn.tursom.core.context


interface Context {
  val envId: Int

  operator fun get(id: Int): Any?
  operator fun <T> get(key: DefaultContextKey<T>): T {
    var value = get(key as ContextKey<T>)
    return if (value != null) {
      value
    } else {
      value = key.provider(this)
      set(key, value)
      value
    }
  }

  operator fun <T> get(key: ContextKey<T>): T?
  operator fun <T> set(key: ContextKey<T>, value: T): Context

  fun checkEnv(key: ContextKey<*>) {
    if (envId != key.envId) {
      throw UnsupportedOperationException()
    }
  }
}

