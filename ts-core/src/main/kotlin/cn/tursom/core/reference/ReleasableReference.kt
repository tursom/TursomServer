package cn.tursom.core.reference

import org.slf4j.LoggerFactory
import java.lang.ref.ReferenceQueue
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

interface ReleasableReference {
  companion object {
    private val logger = LoggerFactory.getLogger(ReleasableReference::class.java)

    private val referenceSet: MutableSet<ReleasableReference> =
      ConcurrentHashMap<ReleasableReference, Unit>().keySet(Unit)
    val referenceQueue = ReferenceQueue<Any?>()

    private val freeThread = thread(isDaemon = true) {
      while (true) {
        val freeReference = referenceQueue.remove(1000) ?: continue
        try {
          if (freeReference is ReleasableReference) {
            freeReference.release()
            referenceSet.remove(freeReference)
          }
        } catch (e: Throwable) {
          logger.error("an exception caused on free reference", e)
        }
      }
    }

    fun hosting(reference: ReleasableReference) {
      referenceSet.add(reference)
    }

    fun free(reference: ReleasableReference) {
      referenceSet.remove(reference)
    }
  }

  fun release()
  fun cancel() {
    referenceSet.remove(this)
  }
}
