package cn.tursom.mongodb.async.subscriber

import cn.tursom.core.Disposable
import cn.tursom.mongodb.BsonFactory
import cn.tursom.utils.AsyncIterator
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AsyncIteratorSubscriber<T>(
  bsonFactory: BsonFactory<T>,
  val bufSize: Int = 32,
  requestSize: Long = 1
) : AbstractSubscriber<T>(bsonFactory, requestSize), AsyncIterator<T>, BsonFactory<T> by bsonFactory {
  private var cont = Disposable<Continuation<T>>()
  private var notify = Disposable<Continuation<Unit>>()
  private val cache = ConcurrentLinkedQueue<T>()

  override fun onComplete() {
    super.onComplete()
    cont.get()?.resumeWithException(Exception())
    notify.get()?.resume(Unit)
  }

  override fun next(t: T) {
    cont.get()?.resume(t) ?: cache.add(t)
    notify.get()?.resume(Unit)
  }

  override fun onError(t: Throwable) {
    cont.get()?.resumeWithException(t) ?: t.printStackTrace()
  }

  override suspend fun next(): T {
    return cache.poll() ?: suspendCoroutine { cont ->
      this.cont.set(cont)
      s.request(bufSize.toLong())
      cache.poll()?.let { this.cont.get()?.resume(it) ?: cache.add(it) }
    }
  }

  override suspend fun hasNext(): Boolean {
    if (cache.isEmpty() && !compete) {
      suspendCoroutine<Unit> {
        notify.set(it)
        s.request(bufSize.toLong())
        if (cache.isNotEmpty()) {
          notify.get()?.resume(Unit)
        }
      }
    }
    return cache.isNotEmpty()
  }
}