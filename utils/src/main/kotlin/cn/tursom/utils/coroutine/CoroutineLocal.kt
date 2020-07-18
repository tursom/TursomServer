package cn.tursom.utils.coroutine

import cn.tursom.core.cast
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

open class CoroutineLocal<T> {
  open suspend fun get(): T? {
    var attach: MutableMap<CoroutineLocal<*>, Any?>? = coroutineContext[CoroutineLocalContext]
    if (attach == null) {
      val job = coroutineContext[Job] ?: return null
      attach = attachMap[job]
    }
    return attach?.get(this)?.cast()
  }

  open suspend infix fun set(value: T): Boolean {
    var attach: MutableMap<CoroutineLocal<*>, Any?>? = coroutineContext[CoroutineLocalContext]
    if (attach == null) {
      val job = coroutineContext[Job] ?: return false
      attach = attachMap[job]
      if (attach == null) {
        attach = HashMap()
        attachMap[job] = attach
        job.invokeOnCompletion {
          attachMap.remove(job)
        }
      }
    }
    attach[this] = value
    return true
  }

  companion object {
    private val attachMap = ConcurrentHashMap<CoroutineContext, MutableMap<CoroutineLocal<*>, Any?>>()
    override fun toString(): String = attachMap.toString()
  }
}