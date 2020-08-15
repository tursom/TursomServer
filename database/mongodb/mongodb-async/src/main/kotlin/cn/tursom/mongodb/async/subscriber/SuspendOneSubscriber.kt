package cn.tursom.mongodb.async.subscriber

import cn.tursom.core.Disposable
import cn.tursom.mongodb.BsonFactory
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ReactiveStreamsSubscriberImplementation")
class SuspendOneSubscriber<T>(
  bsonFactory: BsonFactory<T>,
  cont: Continuation<T?>,
  size: Long = 1
) : AbstractSubscriber<T>(bsonFactory, size) {
  val contDisposable = Disposable(cont)
  override fun onComplete() {
    contDisposable.get()?.resume(null)
  }

  override fun next(t: T) {
    contDisposable.get()?.resume(t)
  }

  override fun onError(t: Throwable) {
    contDisposable.get()?.resumeWithException(t)
  }
}