package cn.tursom.mongodb.async.subscriber

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ReactiveStreamsSubscriberImplementation")
class SuspendInsertListSubscriber<T>(
  val cont: Continuation<List<T>>,
  val requestSize: Long = Long.MAX_VALUE
) : Subscriber<T> {
  val resultList = ArrayList<T>()
  override fun onComplete() {
    cont.resume(resultList)
  }

  override fun onSubscribe(s: Subscription) = s.request(requestSize)
  override fun onNext(t: T) {
    resultList.add(t)
  }

  override fun onError(t: Throwable) {
    cont.resumeWithException(t)
  }
}