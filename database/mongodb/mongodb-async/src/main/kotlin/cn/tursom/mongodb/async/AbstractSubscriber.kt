package cn.tursom.mongodb.async

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

abstract class AbstractSubscriber<T>(val autoRequest: Boolean = false) : Subscriber<T> {
  var compete = false
  lateinit var subscription: Subscription

  override fun onComplete() {
    compete = true
  }

  override fun onSubscribe(s: Subscription) {
    subscription = s
    if (autoRequest) {
      subscription.request(Int.MAX_VALUE.toLong())
    }
  }
}