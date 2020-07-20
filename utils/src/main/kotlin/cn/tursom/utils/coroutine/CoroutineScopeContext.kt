package cn.tursom.utils.coroutine

import cn.tursom.core.cast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CoroutineScopeContext(
  var coroutineScope: CoroutineScope = GlobalScope
) : CoroutineContext.Element {
  override val key: CoroutineContext.Key<*> get() = Companion

  override fun toString(): String = "CoroutineScopeContext(coroutineScope=$coroutineScope)"

  companion object : CoroutineContext.Key<CoroutineScopeContext>, CoroutineLocal<CoroutineScope>() {
    override suspend fun get(): CoroutineScope = coroutineContext[this]?.coroutineScope ?: super.get()
    ?: coroutineContext[Job]?.let {
      if (it is CoroutineScope) {
        it.cast<CoroutineScope>()
      } else {
        null
      }
    } ?: CoroutineContextScope(coroutineContext)

    override suspend infix fun set(value: CoroutineScope): Boolean {
      val coroutineScopeContext = coroutineContext[this]
      return if (coroutineScopeContext != null) {
        coroutineScopeContext.coroutineScope = value
        true
      } else {
        super.set(value)
      }
    }
  }
}