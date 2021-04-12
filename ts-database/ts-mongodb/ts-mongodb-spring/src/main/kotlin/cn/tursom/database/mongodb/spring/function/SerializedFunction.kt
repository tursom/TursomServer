package cn.tursom.database.mongodb.spring.function

import java.io.Serializable
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Field
import java.lang.reflect.Method

interface SerializedFunction : Serializable

private val getterRegex = Regex("^get[A-Z].*")
private val booleanGetterRegex = Regex("^is[A-Z].*")

val SerializedFunction.serializedLambda: SerializedLambda
  get() {
    val writeReplace = javaClass.getDeclaredMethod("writeReplace")
    writeReplace.isAccessible = true
    return writeReplace.invoke(this) as SerializedLambda
  }

val SerializedFunction.implClassName: String get() = serializedLambda.implClass.replace('/', '.')

val SerializedFunction.implClass: Class<*> get() = Class.forName(implClassName)
val SerializedFunction.implMethodName: String get() = serializedLambda.implMethodName

val SerializedFunction.implMethod: Method get() = implClass.getMethod(serializedLambda.implMethodName)
val SerializedFunction.fieldName: String?
  get() {
    val methodName = implMethodName
    return getFieldNameFromGetterName(methodName)
  }

val SerializedFunction.field: Field?
  get() {
    val fieldName = fieldName ?: return null
    val clazz = implClass
    return clazz.getDeclaredField(fieldName)
  }

fun getFieldNameFromGetterName(getterName: String): String? = when {
  getterRegex.matches(getterName) -> {
    val chars = getterName.toCharArray()
    chars[3] = Character.toLowerCase(chars[3])
    String(chars, 3, chars.size - 3)
  }
  booleanGetterRegex.matches(getterName) -> {
    val chars = getterName.toCharArray()
    chars[2] = Character.toLowerCase(chars[2])
    String(chars, 2, chars.size - 2)
  }
  else -> getterName
}