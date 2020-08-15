package cn.tursom.mongodb.async.subscriber

import cn.tursom.mongodb.BsonFactory
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ReactiveStreamsSubscriberImplementation")
class SuspendListSubscriber<T>(
  bsonFactory: BsonFactory<T>,
  val cont: Continuation<List<T>>,
  size: Long = Long.MAX_VALUE
) : AbstractSubscriber<T>(bsonFactory, size) {
  val resultList = ArrayList<T>()
  override fun onComplete() = cont.resume(resultList)
  override fun onError(t: Throwable) = cont.resumeWithException(t)

  override fun next(t: T) {
    resultList.add(t)
  }
}