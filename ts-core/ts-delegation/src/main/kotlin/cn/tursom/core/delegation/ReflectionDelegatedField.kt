package cn.tursom.core.delegation

import cn.tursom.core.getFieldForAll
import cn.tursom.core.uncheckedCast
import java.lang.reflect.Field

class ReflectionDelegatedField<in T, V>(
  private val receiver: T,
  private val field: Field,
) : MutableDelegatedField<T, V> {
  init {
    field.isAccessible = true
  }

  override fun getValue(): V = field.get(receiver).uncheckedCast()

  override fun setValue(value: V) {
    field.set(receiver, value)
  }

  companion object {
    fun <T : Any, V> T.superField(
      fieldName: String,
    ): MutableDelegatedField<T, V> {
      return ReflectionDelegatedField(this, this.javaClass.getFieldForAll(fieldName)!!)
    }

    fun <T, V> T.field(
      field: Field,
    ): MutableDelegatedField<T, V> {
      return ReflectionDelegatedField(this, field)
    }

    fun <T, V> T.field(
      field: Field,
      type: V,
    ): MutableDelegatedField<T, V> {
      return ReflectionDelegatedField(this, field)
    }

    inline fun <T, V> T.field(
      field: Field,
      type: () -> V,
    ): MutableDelegatedField<T, V> {
      return ReflectionDelegatedField(this, field)
    }
  }
}

