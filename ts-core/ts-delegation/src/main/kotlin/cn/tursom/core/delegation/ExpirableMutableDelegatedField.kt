package cn.tursom.core.delegation

import cn.tursom.core.uncheckedCast
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty

class ExpirableMutableDelegatedField<in T, V : Any>(
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
      delegatedField.setValue(value)
      setTime = System.currentTimeMillis()
    }
  }
}

fun <T, V : Any> MutableDelegatedField<T, V>.expirable(
  expireTime: Long,
  timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
): MutableDelegatedField<T, V?> {
  return ExpirableMutableDelegatedField(this, timeUnit.toMillis(expireTime))
}

@JvmName("expirableTV?")
fun <T, V : Any> MutableDelegatedField<T, V?>.expirable(
  expireTime: Long,
  timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
): MutableDelegatedField<T, V?> {
  return ExpirableMutableDelegatedField(uncheckedCast(), timeUnit.toMillis(expireTime))
}

