package cn.tursom.mongodb.async.subscriber

import cn.tursom.mongodb.BsonFactory
import org.bson.Document
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

@Suppress("ReactiveStreamsSubscriberImplementation")
abstract class AbstractSubscriber<T>(
  private val bsonFactory: BsonFactory<T>,
  val size: Long = 1
) : Subscriber<Document> {
  abstract fun next(t: T)

  protected var requested = 0
  protected lateinit var s: Subscription
  protected var compete = false

  override fun onComplete() {
    compete = true
  }

  override fun onSubscribe(s: Subscription) {
    this.s = s
    s.request(size - requested)
  }

  override fun onNext(t: Document) {
    next(bsonFactory.parse(t))
    requested++
    if (size - requested > 0) {
      s.request(size - requested)
    } else {
      onComplete()
    }
  }
}