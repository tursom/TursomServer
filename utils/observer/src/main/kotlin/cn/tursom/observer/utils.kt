package cn.tursom.observer


import cn.tursom.core.cast
import cn.tursom.core.receiver
import cn.tursom.delegation.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

@Observable
fun <V> KMutableProperty0<V>.listenable(): MutableDelegatedField<Any, V> {
  isAccessible = true
  val delegate = getDelegate()
  return if (delegate is MutableDelegatedField<*, *> && delegate[ObservableMutableDelegatedField] == true) {
    delegate.cast()
  } else {
    ObservableMutableDelegatedField(KPropertyMutableDelegatedField(cast()))
  }
}

@Observable
fun <V> listenable(initValue: V): MutableDelegatedField<Any, V> = ObservableMutableDelegatedField(
  MutableDelegatedFieldValue(initValue)
)

@Observable
fun <T, V> MutableDelegatedField<T, V>.listenable(): MutableDelegatedField<T, V> =
  ObservableMutableDelegatedField(this)

@OptIn(Observable::class)
fun <V> KProperty0<V>.getListenableMutableDelegatedField(): ObservableMutableDelegatedField<Any, V>? {
  isAccessible = true
  var delegate = getDelegate() ?: getDelegate(receiver, name)
  if (delegate is DelegatedField<*, *>) {
    while (true) {
      if (delegate is ObservableMutableDelegatedField<*, *>) {
        return delegate.cast()
      }
      if (delegate is DecoratorDelegatedField<*, *>) {
        delegate = delegate.delegatedField
      } else {
        break
      }
    }
  } else if (delegate is KProperty0<*>) {
    return delegate.cast<KProperty0<V>>().getListenableMutableDelegatedField()
  }
  return null
}

inline fun <T, V> T.addChangeListener(
  property: T.() -> KProperty0<V>,
): ObservableObserver<T, V> {
  val kProperty0 = property()

  @OptIn(Observable::class)
  val delegatedField = kProperty0
    .getListenableMutableDelegatedField()
    .cast<ObservableMutableDelegatedField<T, V>?>()
    ?: throw UnmonitoredFieldException(kProperty0.toString())
  return object : ObservableObserver<T, V> {
    private val selfList = ConcurrentLinkedQueue<Observer<T, V>>()
    override fun addListener(listener: T.(old: V, new: V) -> Unit): Observer<T, V> {
      @OptIn(Observable::class)
      val listener1 = delegatedField.addListener(listener)
      selfList.add(listener1)
      return listener1
    }

    override fun catch(handler: T.(old: V, new: V, e: Throwable) -> Unit): ObservableObserver<T, V> {
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

    override fun finally(handler: T.(old: V, new: V) -> Unit): Observer<T, V> {
      selfList.forEach {
        it.finally(handler)
      }
      return this
    }
  }
}


infix operator fun <T, V> ObservableObserver<T, V>.invoke(listener: T.(old: V, new: V) -> Unit): Observer<T, V> =
  addListener(listener)

infix fun <T, V> ObservableObserver<T, V>.with(listener: T.(old: V, new: V) -> Unit): Observer<T, V> =
  addListener(listener)

infix fun <T, V> ObservableObserver<T, V>.and(listener: T.(old: V, new: V) -> Unit): Observer<T, V> =
  addListener(listener)

infix operator fun <T, V> ObservableObserver<T, V>.plus(listener: T.(old: V, new: V) -> Unit): Observer<T, V> =
  addListener(listener)

infix fun <V> KProperty0<V>.listen(listener: Any.(old: V, new: V) -> Unit): Observer<Any, V> {
  @OptIn(Observable::class)
  return getListenableMutableDelegatedField()
    ?.addListener(listener)
    ?: throw UnmonitoredFieldException(toString())
}

fun <T, V> T.listen(property: KProperty0<V>, listener: T.(old: V, new: V) -> Unit): Observer<Any, V> {
  @OptIn(Observable::class)
  return property.getListenableMutableDelegatedField()
    ?.addListener(listener.cast())
    ?: throw UnmonitoredFieldException(toString())
}
