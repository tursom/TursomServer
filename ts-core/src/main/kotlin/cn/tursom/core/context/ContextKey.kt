package cn.tursom.core.context

import cn.tursom.core.util.uncheckedCast

open class ContextKey<T>(
  val envId: Int,
  val id: Int,
) {
  fun withDefault(provider: ContextKey<T>.(Context) -> T) = DefaultContextKey(envId, id, provider)
  fun withSynchronizedDefault(provider: ContextKey<T>.(Context) -> T) = DefaultContextKey<T>(envId, id) { context ->
    synchronized(context) {
      context[id]?.uncheckedCast() ?: run {
        val value = provider(context)
        context[this] = value
        value
      }
    }
  }

  override fun hashCode(): Int {
    return id
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ContextKey<*>) return false

    if (envId != other.envId) return false
    if (id != other.id) return false

    return true
  }

  override fun toString(): String {
    return "ContextKey(envId=$envId, id=$id)"
  }
}
