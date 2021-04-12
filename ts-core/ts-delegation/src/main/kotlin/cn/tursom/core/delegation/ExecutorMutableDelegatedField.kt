package cn.tursom.core.delegation

import java.util.concurrent.Executor
import kotlin.reflect.KProperty

class ExecutorMutableDelegatedField<in T, V>(
  override val delegatedField: MutableDelegatedField<T, V>,
  private val executor: Executor,
) : MutableDelegatedField<T, V> by delegatedField, DecoratorMutableDelegatedField<T, V> {
    override fun valueOnSet(thisRef: T, property: KProperty<*>, value: V, oldValue: V) {
        executor.execute {
            delegatedField.valueOnSet(thisRef, property, value, oldValue)
        }
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        valueOnSet(thisRef, property, value, getValue())
        delegatedField.setValue(value)
    }
}
