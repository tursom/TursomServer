package cn.tursom.core.reference

import org.slf4j.LoggerFactory
import java.lang.ref.ReferenceQueue
import kotlin.concurrent.thread

interface ReleasableReference {
  companion object {
    private val logger = LoggerFactory.getLogger(ReleasableReference::class.java)
    val referenceQueue = ReferenceQueue<Any?>()
    private val freeThread = thread(isDaemon = true) {
      while (true) {
        val freeReference = referenceQueue.remove(1000) ?: continue
        try {
          if (freeReference is ReleasableReference) {
            freeReference.release()
          }
        } catch (e: Throwable) {
          logger.error("an exception caused on free reference", e)
        }
      }
    }
  }

  fun release()
}
