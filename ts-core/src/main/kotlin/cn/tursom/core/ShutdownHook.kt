package cn.tursom.core

import com.sun.org.slf4j.internal.LoggerFactory
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicInteger

/**
 * Runtime.getRuntime().addShutdownHook() 的高效版本
 * 使用线程池免去了线程创建的开销
 */
@Suppress("unused")
object ShutdownHook {
  private val logger = LoggerFactory.getLogger(ShutdownHook::class.java)

  internal interface HookReference {
    fun get(): (() -> Unit)?
  }

  class Hook internal constructor(
    private val hook: () -> Unit,
    private val reference: HookReference,
  ) {
    fun cancel() {
      shutdownHooks.remove(reference)
    }
  }

  private class SoftReference(
    hook: () -> Unit,
  ) : FreeReference<() -> Unit>(hook) {
    override fun free() {
      shutdownHooks.removeIf {
        it.get() == null
      }
    }
  }

  private val shutdownHooks = ConcurrentLinkedDeque<HookReference>()
  private val availableThreadCount = Runtime.getRuntime().availableProcessors() * 2
  private val activeThreadCount = AtomicInteger()

  fun addHook(softReference: Boolean = false, hook: () -> Unit): Hook {
    if (activeThreadCount.incrementAndGet() <= availableThreadCount) {
      addWorkThread()
    }

    val reference = if (softReference) object : HookReference {
      private val ref = SoftReference(hook)
      override fun get(): (() -> Unit)? = ref.get()
    } else object : HookReference {
      override fun get(): () -> Unit = hook
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
          logger.error("an exception caused on hook", e)
        }
        hook = shutdownHooks.poll()
      }
    })
  }
}
