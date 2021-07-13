package cn.tursom.core

import org.slf4j.LoggerFactory
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import kotlin.concurrent.thread


abstract class FreeReference<T>(referent: T) : PhantomReference<T>(referent, referenceQueue) {
  companion object {
    private val logger = LoggerFactory.getLogger(FreeReference::class.java)
    private val referenceQueue = ReferenceQueue<Any?>()
    private val freeThread = thread(isDaemon = true) {
      while (true) {
        val freeReference = referenceQueue.remove(1000) ?: continue
        try {
          if (freeReference is FreeReference<*> && !freeReference.cancel) {
            freeReference.free()
          }
        } catch (e: Throwable) {
          logger.error("an exception caused on free reference", e)
        }
      }
    }
  }

  private var cancel: Boolean = false

  override fun enqueue(): Boolean {
    return if (cancel) {
      false
    } else {
      super.enqueue()
    }
  }

  abstract fun free()
  fun cancel() {
    cancel = true
  }
}
