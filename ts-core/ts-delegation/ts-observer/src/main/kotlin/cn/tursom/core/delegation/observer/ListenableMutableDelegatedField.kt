package cn.tursom.core.delegation.observer

import cn.tursom.core.delegation.DecoratorMutableDelegatedField
import cn.tursom.core.delegation.DelegatedFieldAttachmentKey
import cn.tursom.core.delegation.MutableDelegatedField
import cn.tursom.core.uncheckedCast
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KProperty

@Listenable
class ListenableMutableDelegatedField<T, V>(
  override val delegatedField: MutableDelegatedField<T, V>,
) : MutableDelegatedField<T, V> by delegatedField, DecoratorMutableDelegatedField<T, V> {
  companion object : DelegatedFieldAttachmentKey<Boolean>

  override fun <K> get(key: DelegatedFieldAttachmentKey<K>): K? {
    return if (key == Companion) true.uncheckedCast() else super.get(key)
  }

  private val listenerList = ConcurrentLinkedDeque<T.(old: V, new: V) -> Unit>()

  override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    val oldValue = getValue()
    listenerList.forEach {
      thisRef.it(oldValue, value)
    }
    delegatedField.setValue(thisRef, property, value)
  }

  override fun valueOnSet(thisRef: T, property: KProperty<*>, value: V, oldValue: V) {
    listenerList.forEach {
      thisRef.it(oldValue, value)
    }
    delegatedField.valueOnSet(thisRef, property, value, oldValue)
  }

  fun addListener(listener: T.(old: V, new: V) -> Unit): Listener<T, V> {
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
    return object : Listener<T, V> {
      override fun cancel() = listenerList.remove(listener)

      override fun catch(handler: T.(old: V, new: V, e: Throwable) -> Unit): Listener<T, V> {
        catch = handler
        return this
      }

      override fun finally(handler: T.(old: V, new: V) -> Unit): Listener<T, V> {
        finally = handler
        return this
      }

    }
  }
}