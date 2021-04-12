package cn.tursom.database.mongodb.subscriber

import cn.tursom.core.Disposable
import cn.tursom.database.mongodb.MongoUtil
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.Executor
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ReactiveStreamsSubscriberImplementation")
class SuspendInsertOneSubscriber<T>(
  cont: Continuation<T?>,
  val requestSize: Long = 1,
  val subscribeExecutor: Executor = MongoUtil.mongoExecutor,
) : Subscriber<T> {
  val contDisposable = Disposable(cont)
  override fun onComplete() {
    contDisposable.get()?.resume(null)
  }

  override fun onSubscribe(s: Subscription) = if (requestSize > 0) {
    subscribeExecutor.execute { s.request(requestSize) }
  } else onComplete()

  override fun onNext(t: T) {
    contDisposable.get()?.resume(t)
  }

  override fun onError(t: Throwable) {
    contDisposable.get()?.resumeWithException(t)
  }
}