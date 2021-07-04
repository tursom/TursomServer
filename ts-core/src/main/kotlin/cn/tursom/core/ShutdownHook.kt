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

  private val shutdownHooks = ConcurrentLinkedDeque<() -> Unit>()
  private val availableThreadCount = Runtime.getRuntime().availableProcessors() * 2
  private val activeThreadCount = AtomicInteger()

  fun addHook(hook: () -> Unit): Boolean {
    if (activeThreadCount.incrementAndGet() <= availableThreadCount) {
      addWorkThread()
    }
    return shutdownHooks.add(hook)
  }

  private fun addWorkThread() {
    Runtime.getRuntime().addShutdownHook(Thread {
      var hook = shutdownHooks.poll()
      while (hook != null) {
        try {
          hook()
        } catch (e: Throwable) {
          //error("an exception caused on hook", e)
        }
        hook = shutdownHooks.poll()
      }
    })
  }
}
