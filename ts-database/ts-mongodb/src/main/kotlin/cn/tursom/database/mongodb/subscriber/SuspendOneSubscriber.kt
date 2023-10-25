package cn.tursom.database.mongodb.subscriber

import cn.tursom.core.util.Disposable
import cn.tursom.database.mongodb.BsonFactory
import cn.tursom.database.mongodb.MongoUtil
import java.util.concurrent.Executor
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ReactiveStreamsSubscriberImplementation")
class SuspendOneSubscriber<T>(
  bsonFactory: BsonFactory<T>,
  cont: Continuation<T?>,
  size: Long = 1,
  subscribeExecutor: Executor = MongoUtil.mongoExecutor,
) : AbstractSubscriber<T>(bsonFactory, size, subscribeExecutor) {
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