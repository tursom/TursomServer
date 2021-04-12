package cn.tursom.core.delegation

import cn.tursom.core.uncheckedCast
import kotlin.reflect.KProperty

/**
 * 当从上一级 MutableDelegatedField 获得到空值时
 * 自动调用updateValue方法更新值
 */
class AutoUpdatableMutableDelegatedField<in T, V : Any>(
    override val delegatedField: MutableDelegatedField<T, V?>,
    val updateValue: T.(property: KProperty<*>) -> V,
) : MutableDelegatedField<T, V> by delegatedField.uncheckedCast(),
    DecoratorMutableDelegatedField<T, V?> {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        var value = delegatedField.getValue(thisRef, property)
        if (value == null) {
            value = thisRef.updateValue(property)
            delegatedField.setValue(thisRef, property, value)
        }
        return value
    }
}

fun <T, V : Any> MutableDelegatedField<T, V?>.autoUpdate(
    updateValue: T.(property: KProperty<*>) -> V,
): MutableDelegatedField<T, V> {
    return AutoUpdatableMutableDelegatedField(this, updateValue)
}