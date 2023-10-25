package cn.tursom.core.delegation

import cn.tursom.core.util.final
import cn.tursom.core.util.getFieldForAll
import cn.tursom.core.util.uncheckedCast
import java.lang.reflect.Field

class ReflectionDelegatedField<in T, V>(
  private val receiver: T,
  private val field: Field,
) : MutableDelegatedField<T, V> {
  init {
    field.isAccessible = true
    field.final = false
  }

  override fun getValue(): V = field.get(receiver).uncheckedCast()

  override fun setValue(value: V) {
    field.set(receiver, value)
  }

  override fun toString(): String {
    return "ReflectionDelegatedField(receiver=$receiver, field=$field)"
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

    @Suppress("UNUSED_PARAMETER")
    fun <T, V> T.field(
      field: Field,
      type: V,
    ): MutableDelegatedField<T, V> {
      return ReflectionDelegatedField(this, field)
    }

    @Suppress("UNUSED_PARAMETER")
    inline fun <T, V> T.field(
      field: Field,
      type: () -> V,
    ): MutableDelegatedField<T, V> {
      return ReflectionDelegatedField(this, field)
    }
  }
}
