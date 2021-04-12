package cn.tursom.database.mongodb.subscriber

import cn.tursom.database.mongodb.BsonFactory
import cn.tursom.database.mongodb.MongoUtil
import java.util.concurrent.Executor
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ReactiveStreamsSubscriberImplementation")
class SuspendListSubscriber<T>(
  bsonFactory: BsonFactory<T>,
  val cont: Continuation<List<T>>,
  size: Long = Long.MAX_VALUE,
  subscribeExecutor: Executor = MongoUtil.mongoExecutor,
) : AbstractSubscriber<T>(bsonFactory, size, subscribeExecutor) {
  companion object {
    const val maxDefaultArrayCache = 4096
  }

  val resultList = ArrayList<T>(if (size < maxDefaultArrayCache) size.toInt() else maxDefaultArrayCache)
  override fun onComplete() = cont.resume(resultList)
  override fun onError(t: Throwable) = cont.resumeWithException(t)

  override fun next(t: T) {
    resultList.add(t)
  }
}