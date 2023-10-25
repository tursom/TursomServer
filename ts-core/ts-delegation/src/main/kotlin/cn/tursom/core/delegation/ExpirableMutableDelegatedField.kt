package cn.tursom.core.delegation

import cn.tursom.core.util.uncheckedCast
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty

class ExpirableMutableDelegatedField<in T, V>(
  override val delegatedField: MutableDelegatedField<T, V>,
  val expireMS: Long,
) : MutableDelegatedField<T, V?> by delegatedField.uncheckedCast(),
  DecoratorMutableDelegatedField<T, V> {

  @Volatile
  private var setTime: Long = 0L

  override fun getValue(thisRef: T, property: KProperty<*>): V? {
    return if (System.currentTimeMillis() - setTime < expireMS) {
      delegatedField.uncheckedCast<MutableDelegatedField<T, V?>>().getValue(thisRef, property)
    } else {
      null
    }
  }

  override fun setValue(thisRef: T, property: KProperty<*>, value: V?) {
    if (value != null) {
      delegatedField.setValue(thisRef, property, value)
      setTime = System.currentTimeMillis()
    }
  }

  override fun valueOnSet(thisRef: T, property: KProperty<*>, value: V?, oldValue: V?) {
    if (value != null) {
      setTime = System.currentTimeMillis()
    }
  }
}

fun <T, V> MutableDelegatedField<T, V>.expirable(
  expireTime: Long,
  timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
): MutableDelegatedField<T, V?> {
  return ExpirableMutableDelegatedField(this, timeUnit.toMillis(expireTime))
}
