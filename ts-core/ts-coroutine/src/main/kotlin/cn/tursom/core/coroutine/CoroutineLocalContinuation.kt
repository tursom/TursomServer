package cn.tursom.core.coroutine

import cn.tursom.core.util.cast
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineLocalContinuation(
  private val completion: Continuation<*>,
) : Continuation<Any?> by completion.cast() {
  override val context: CoroutineContext = completion.context + if (completion.context[CoroutineLocalContext] == null) {
    CoroutineLocalContext()
  } else {
    EmptyCoroutineContext
  }
}