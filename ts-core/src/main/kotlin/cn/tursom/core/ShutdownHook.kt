package cn.tursom.core

import com.sun.org.slf4j.internal.LoggerFactory
import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicInteger

/**
 * Runtime.getRuntime().addShutdownHook() 的高效版本
 * 使用线程池免去了线程创建的开销
 */
@Suppress("unused")
object ShutdownHook {
  private val logger = LoggerFactory.getLogger(ShutdownHook::class.java)

  private val shutdownHooks = ConcurrentLinkedDeque<Reference<(() -> Unit)?>>()
  private val availableThreadCount = Runtime.getRuntime().availableProcessors() * 2
  private val activeThreadCount = AtomicInteger()

  interface Reference<out T> {
    fun get(): T
  }

  class Hook(
    private val hook: () -> Unit,
    private val reference: Reference<(() -> Unit)?>
  ) {
    fun cancel() {
      shutdownHooks.remove(reference)
    }
  }

  fun addHook(softReference: Boolean = false, hook: () -> Unit): Hook {
    if (activeThreadCount.incrementAndGet() <= availableThreadCount) {
      addWorkThread()
    }

    val reference = if (softReference) {
      object : Reference<(() -> Unit)?> {
        private val ref = SoftReference(hook)
        override fun get(): (() -> Unit)? = ref.get()
      }
    } else {
      object : Reference<() -> Unit> {
        override fun get(): () -> Unit = hook
      }
    }
    shutdownHooks.add(reference)
    return Hook(hook, reference)
  }

  private fun addWorkThread() {
    Runtime.getRuntime().addShutdownHook(Thread {
      var hook = shutdownHooks.poll()
      while (hook != null) {
        try {
          hook.get()?.invoke()
        } catch (e: Throwable) {
          //error("an exception caused on hook", e)
        }
        hook = shutdownHooks.poll()
      }
    })
  }
}
