package cn.tursom.core.context

class DefaultContextKey<T>(
  envId: Int,
  id: Int,
  private val realProvider: ContextKey<T>.(Context) -> T,
) : ContextKey<T>(envId, id) {
  val provider: (Context) -> T = {
    realProvider(it)
  }
}
