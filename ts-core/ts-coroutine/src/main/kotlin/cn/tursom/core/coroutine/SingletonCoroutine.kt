package cn.tursom.core.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class SingletonCoroutine(
  private val scope: CoroutineScope = GlobalScope,
  private val handler: suspend CoroutineScope.() -> Unit
) : Runnable {
  private val run = AtomicBoolean()

  override fun run() = run(EmptyCoroutineContext)

  fun run(context: CoroutineContext) {
    if (!run.compareAndSet(false, true)) {
      return
    }
    scope.launch(context) {
      try {
        handler()
      } finally {
        run.set(false)
      }
    }
  }
}
