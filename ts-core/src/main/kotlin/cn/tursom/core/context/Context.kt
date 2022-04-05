package cn.tursom.core.context


interface Context {
  val envId: Int
  operator fun <T> get(key: ContextKey<T>): T?
  operator fun <T> set(key: ContextKey<T>, value: T)

  fun checkEnv(key: ContextKey<*>) {
    if (envId != key.envId) {
      throw UnsupportedOperationException()
    }
  }
}

