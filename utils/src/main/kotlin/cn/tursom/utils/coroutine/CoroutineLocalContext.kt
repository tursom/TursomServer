package cn.tursom.utils.coroutine

import kotlin.coroutines.CoroutineContext

open class CoroutineLocalContext(
  private val map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4)
) : CoroutineContext.Element, MutableMap<CoroutineLocal<*>, Any?> by map {
  override val key: CoroutineContext.Key<*> get() = Companion

  override fun toString(): String {
    return "CoroutineLocalContext$map"
  }

  companion object : CoroutineContext.Key<CoroutineLocalContext>
}