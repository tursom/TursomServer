package cn.tursom.database.mongodb.subscriber

import cn.tursom.core.Disposable
import cn.tursom.core.datastruct.AsyncIterator
import cn.tursom.database.mongodb.BsonFactory
import cn.tursom.database.mongodb.MongoUtil
import org.bson.Document
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("ReactiveStreamsSubscriberImplementation")
class AsyncIteratorSubscriber<T>(
  private val bsonFactory: BsonFactory<T>,
  val bufSize: Int = 32,
  val subscribeExecutor: Executor = MongoUtil.mongoExecutor,
) : Subscriber<Document>, AsyncIterator<T>, BsonFactory<T> by bsonFactory {
  protected lateinit var s: Subscription

  @Volatile
  protected var compete = false

  private var cont = Disposable<Continuation<T>>()
  private var notify = Disposable<Continuation<Unit>>()
  private val cache = ConcurrentLinkedQueue<T>()

  override fun onComplete() {
    cont.get()?.resumeWithException(Exception())
    notify.get()?.resume(Unit)
    compete = true
  }

  override fun onNext(t: Document) {
    next(bsonFactory.parse(t))
  }

  override fun onSubscribe(s: Subscription) {
    this.s = s
    subscribeExecutor.execute {
      s.request(1)
    }
  }

  fun next(t: T) {
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