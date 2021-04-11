package cn.tursom.core

import java.lang.ref.PhantomReference
import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

object Finalized {
  private val referenceQueue = ReferenceQueue<Any?>()
  private val handlerMap = ConcurrentHashMap<Reference<*>, () -> Unit>()

  init {
    thread(isDaemon = true) {
      while (true) {
        val action = handlerMap.remove(referenceQueue.remove() ?: return@thread) ?: continue
        try {
          action()
        } catch (e: Exception) {
        }
      }
    }
  }

  fun <T> listen(obj: T, action: () -> Unit): Reference<T> = PhantomReference(obj, referenceQueue).also {
    handlerMap[it] = action
  }

  fun remove(reference: Reference<*>) {
    handlerMap.remove(reference)
  }
}

fun <T> T.finalized(action: () -> Unit) = Finalized.listen(this, action)