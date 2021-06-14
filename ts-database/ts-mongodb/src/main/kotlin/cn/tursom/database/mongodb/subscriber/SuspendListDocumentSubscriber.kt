package cn.tursom.database.mongodb.subscriber

import cn.tursom.database.mongodb.MongoUtil
import org.bson.Document
import java.util.concurrent.Executor
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ReactiveStreamsSubscriberImplementation")
class SuspendListDocumentSubscriber(
  val cont: Continuation<List<Document>>,
  size: Long = Long.MAX_VALUE,
  subscribeExecutor: Executor = MongoUtil.mongoExecutor,
) : AbstractDocumentSubscriber(size, subscribeExecutor) {
  companion object {
    const val maxDefaultArrayCache = 4096
  }

  val resultList = ArrayList<Document>(if (size < maxDefaultArrayCache) size.toInt() else maxDefaultArrayCache)
  override fun onComplete() = cont.resume(resultList)
  override fun onError(t: Throwable) = cont.resumeWithException(t)

  override fun next(t: Document) {
    resultList.add(t)
  }
}