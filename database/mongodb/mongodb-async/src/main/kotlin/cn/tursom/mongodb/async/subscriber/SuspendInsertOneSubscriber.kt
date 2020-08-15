package cn.tursom.mongodb.async.subscriber

import cn.tursom.core.Disposable
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ReactiveStreamsSubscriberImplementation")
class SuspendInsertOneSubscriber<T>(
  cont: Continuation<T?>,
  val requestSize: Long = 1
) : Subscriber<T> {
  val contDisposable = Disposable(cont)
  override fun onComplete() {
    contDisposable.get()?.resume(null)
  }

  override fun onSubscribe(s: Subscription) = s.request(requestSize)
  override fun onNext(t: T) {
    contDisposable.get()?.resume(t)
  }

  override fun onError(t: Throwable) {
    contDisposable.get()?.resumeWithException(t)
  }
}