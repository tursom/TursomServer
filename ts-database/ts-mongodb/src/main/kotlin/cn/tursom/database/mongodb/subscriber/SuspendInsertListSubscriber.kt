package cn.tursom.database.mongodb.subscriber

import cn.tursom.database.mongodb.MongoUtil
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.Executor
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ReactiveStreamsSubscriberImplementation")
class SuspendInsertListSubscriber<T>(
  val cont: Continuation<List<T>>,
  val requestSize: Long = Long.MAX_VALUE,
  val subscribeExecutor: Executor = MongoUtil.mongoExecutor,
) : Subscriber<T> {
  val resultList = ArrayList<T>()
  override fun onComplete() {
    cont.resume(resultList)
  }

  override fun onSubscribe(s: Subscription) {
    if (requestSize > 0) subscribeExecutor.execute { s.request(requestSize) }
    else onComplete()
  }

  override fun onNext(t: T) {
    resultList.add(t)
  }

  override fun onError(t: Throwable) {
    cont.resumeWithException(t)
  }
}