package cn.tursom.database.mongodb.subscriber

import cn.tursom.database.mongodb.BsonFactory
import cn.tursom.database.mongodb.MongoUtil
import org.bson.Document
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.Executor

@Suppress("ReactiveStreamsSubscriberImplementation")
abstract class AbstractSubscriber<T>(
  private val bsonFactory: BsonFactory<T>,
  val size: Long = 1,
  val subscribeExecutor: Executor = MongoUtil.mongoExecutor,
) : Subscriber<Document> {
  abstract fun next(t: T)

  private var requestRemain = 0L
  protected var requested = 0
  protected lateinit var s: Subscription
  protected var compete = false

  override fun onComplete() {
    compete = true
  }

  override fun onSubscribe(s: Subscription) {
    this.s = s
    requestRemain = size - requested
    subscribeExecutor.execute {
      s.request(requestRemain)
    }
  }

  override fun onNext(t: Document) {
    next(bsonFactory.parse(t))
    requestRemain--
    requested++
    if (requestRemain > 0) return
    if (size - requested > 0) {
      s.request(size - requested)
    } else {
      onComplete()
    }
  }
}