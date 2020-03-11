package cn.tursom.mongodb.async

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

abstract class AbstractSubscriber<T> : Subscriber<T> {
  var compete = false
  lateinit var subscription: Subscription

  override fun onComplete() {
    compete = true
  }

  override fun onSubscribe(s: Subscription) {
    subscription = s
  }
}