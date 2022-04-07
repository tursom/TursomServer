package cn.tursom.reflect.asm

import cn.tursom.core.toUpperCase
import cn.tursom.core.uncheckedCast
import com.esotericsoftware.reflectasm.MethodAccess

class ReflectAsmKtField<T, V>(
  private val getterMethodAccess: MethodAccess,
  private val getterIndex: Int,
  private val setterMethodAccess: MethodAccess?,
  private val setterIndex: Int,
) {
  val settable get() = setterMethodAccess != null
  operator fun get(obj: T): V = getterMethodAccess.invoke(obj, getterIndex).uncheckedCast()
  operator fun set(obj: T, value: V) {
    setterMethodAccess!!.invoke(obj, setterIndex, value)
  }

  companion object {
    inline operator fun <reified T, reified R> get(fieldName: String): ReflectAsmKtField<T, R> {
      val getterName = "get" + fieldName.toUpperCase(0)
      val setterName = "set" + fieldName.toUpperCase(0)
      val (getterMethodAccess, getterIndex) = ReflectAsmUtils.getMethod(T::class.java,
        getterName, returnType = R::class.java)!!
      val (setterMethodAccess, setterIndex) = ReflectAsmUtils.getMethod(T::class.java,
        setterName, R::class.java) ?: (null to -1)
      return ReflectAsmKtField(getterMethodAccess, getterIndex, setterMethodAccess, setterIndex)
    }
  }
}
