package cn.tursom.observer

import cn.tursom.core.cast
import cn.tursom.delegation.DecoratorMutableDelegatedField
import cn.tursom.delegation.DelegatedFieldAttachmentKey
import cn.tursom.delegation.MutableDelegatedField
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KProperty

@Observable
class ObservableMutableDelegatedField<T, V>(
  override val mutableDelegatedField: MutableDelegatedField<T, V>,
) : MutableDelegatedField<T, V> by mutableDelegatedField, DecoratorMutableDelegatedField<T, V> {
  companion object : DelegatedFieldAttachmentKey<Boolean>

  override fun <K> get(key: DelegatedFieldAttachmentKey<K>): K? {
    return if (key == Companion) true.cast() else super.get(key)
  }

  private val listenerList = ConcurrentLinkedDeque<T.(old: V, new: V) -> Unit>()

  override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    val oldValue = getValue()
    listenerList.forEach {
      thisRef.it(oldValue, value)
    }
    mutableDelegatedField.setValue(thisRef, property, value)
  }

  override fun valueOnSet(thisRef: T, property: KProperty<*>, value: V, oldValue: V) {
    listenerList.forEach {
      thisRef.it(oldValue, value)
    }
    mutableDelegatedField.valueOnSet(thisRef, property, value, oldValue)
  }

  fun addListener(listener: T.(old: V, new: V) -> Unit): Observer<T, V> {
    var catch: (T.(old: V, new: V, e: Throwable) -> Unit)? = null
    var finally: (T.(old: V, new: V) -> Unit)? = null
    listenerList.add { old, new ->
      try {
        listener(old, new)
      } catch (e: Throwable) {
        catch?.invoke(this, old, new, e)
      } finally {
        finally?.invoke(this, old, new)
      }
    }
    return object : Observer<T, V> {
      override fun cancel() = listenerList.remove(listener)

      override fun catch(handler: T.(old: V, new: V, e: Throwable) -> Unit): Observer<T, V> {
        catch = handler
        return this
      }

      override fun finally(handler: T.(old: V, new: V) -> Unit): Observer<T, V> {
        finally = handler
        return this
      }

    }
  }
}