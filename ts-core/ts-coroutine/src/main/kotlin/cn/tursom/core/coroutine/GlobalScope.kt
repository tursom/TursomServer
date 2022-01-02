package cn.tursom.core.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object GlobalScope : CoroutineScope {
  override val coroutineContext: CoroutineContext
    get() = EmptyCoroutineContext
}