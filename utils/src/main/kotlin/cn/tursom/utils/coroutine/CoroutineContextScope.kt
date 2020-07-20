package cn.tursom.utils.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CoroutineContextScope(override val coroutineContext: CoroutineContext) : CoroutineScope {
  companion object {
    suspend fun get() = CoroutineContextScope(coroutineContext)
  }
}