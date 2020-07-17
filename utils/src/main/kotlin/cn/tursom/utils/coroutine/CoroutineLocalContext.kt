package cn.tursom.utils.coroutine

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class CoroutineLocalContext(
  private val map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4)
) : CoroutineContext.Element, MutableMap<CoroutineLocal<*>, Any?> by map {
  override val key: CoroutineContext.Key<*> get() = Companion

  override fun toString(): String {
    return map.toString()
  }

  companion object : CoroutineContext.Key<CoroutineLocalContext>
}

fun CoroutineScope.launchWithCoroutineLocalContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> Unit
): Job {
  return launch(context + CoroutineLocalContext(map), start, block)
}

@Suppress("DeferredIsResult")
fun <T> CoroutineScope.asyncWithCoroutineLocalContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> T
): Deferred<T> {
  return async(context + CoroutineLocalContext(map), start, block)
}

suspend fun <T> withCoroutineLocalContext(
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> T
): T {
  return withContext(CoroutineLocalContext(map), block)
}