@file:Suppress("unused")

package cn.tursom.core.delegation.observer

import cn.tursom.core.UncheckedCast
import cn.tursom.core.cast
import cn.tursom.core.delegation.*
import cn.tursom.core.receiver
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible


@Listenable
fun <V> KMutableProperty0<V>.listenable(): MutableDelegatedField<Any, V> {
  isAccessible = true
  val delegate = getDelegate()
  return if (delegate is MutableDelegatedField<*, *> && delegate[ListenableMutableDelegatedField] == true) {
    @OptIn(UncheckedCast::class)
    delegate.cast()
  } else {
    @OptIn(UncheckedCast::class)
    (ListenableMutableDelegatedField(KPropertyMutableDelegatedField(cast())))
  }
}

@Listenable
fun <V> listenable(initValue: V): MutableDelegatedField<Any, V> = ListenableMutableDelegatedField(
  MutableDelegatedFieldValue(initValue)
)

@Listenable
fun <T, V> MutableDelegatedField<T, V>.listenable(): MutableDelegatedField<T, V> =
  ListenableMutableDelegatedField(this)

@JvmName("listenable1")
@Listenable
fun <T, V> listenable(delegatedField: MutableDelegatedField<T, V>): MutableDelegatedField<T, V> =
  ListenableMutableDelegatedField(delegatedField)


infix operator fun <T, V> ListenableListener<T, V>.plus(listener: T.(old: V, new: V) -> Unit): Listener<T, V> =
  addListener(listener)

@OptIn(Listenable::class)
fun <V> KProperty0<V>.getListenableMutableDelegatedField(): ListenableMutableDelegatedField<Any, V>? {
  isAccessible = true
  var delegate = getDelegate() ?: getDelegate(receiver, name)
  if (delegate is DelegatedField<*, *>) {
    while (true) {
      if (delegate is ListenableMutableDelegatedField<*, *>) {
        @OptIn(UncheckedCast::class)
        return delegate.cast()
      }
      if (delegate is DecoratorDelegatedField<*, *>) {
        delegate = delegate.delegatedField
      } else {
        break
      }
    }
  } else if (delegate is KProperty0<*> && delegate != this) {
    @OptIn(UncheckedCast::class)
    return delegate.cast<KProperty0<V>>().getListenableMutableDelegatedField()
  }
  return null
}

inline fun <T, V> T.addChangeListener(
  property: T.() -> KProperty0<V>,
): ListenableListener<T, V> {
  val kProperty0 = property()

  @OptIn(Listenable::class, UncheckedCast::class)
  val delegatedField = kProperty0
    .getListenableMutableDelegatedField()
    .cast<ListenableMutableDelegatedField<T, V>?>()
    ?: throw UnmonitoredFieldException(kProperty0.toString())
  return object : ListenableListener<T, V> {
    private val selfList = ConcurrentLinkedQueue<Listener<T, V>>()
    override fun addListener(listener: T.(old: V, new: V) -> Unit): Listener<T, V> {
      @OptIn(Listenable::class)
      val listener1 = delegatedField.addListener(listener)
      selfList.add(listener1)
      return listener1
    }

    override fun catch(handler: T.(old: V, new: V, e: Throwable) -> Unit): ListenableListener<T, V> {
      selfList.forEach {
        it.catch(handler)
      }
      return this
    }

    override fun cancel(): Boolean {
      selfList.forEach {
        it.cancel()
      }
      return true
    }

    override fun finally(handler: T.(old: V, new: V) -> Unit): Listener<T, V> {
      selfList.forEach {
        it.finally(handler)
      }
      return this
    }
  }
}