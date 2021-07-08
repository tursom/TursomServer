package cn.tursom.core.reflect

import cn.tursom.core.Unsafe.get
import cn.tursom.core.companionObjectInstanceOrNull
import cn.tursom.core.isStatic
import cn.tursom.core.uncheckedCast
import java.lang.reflect.Field
import java.lang.reflect.Method

inline fun <reified T : Annotation> Class<*>.getAnnotation(): T? = getAnnotation(T::class.java)
inline fun <reified T : Annotation> Field.getAnnotation(): T? = getAnnotation(T::class.java)
inline fun <reified T : Annotation> Method.getAnnotation(): T? = getAnnotation(T::class.java)

operator fun Class<*>.contains(obj: Any): Boolean = isInstance(obj)

fun <T> Class<*>.getStaticField(name: String): T? {
  val staticField = getDeclaredField(name)
  if (staticField.isStatic()) {
    staticField.isAccessible = true
    return staticField.get(null).uncheckedCast()
  }

  val companionObjectInstance = kotlin.companionObjectInstanceOrNull
  if (companionObjectInstance != null) {
    return companionObjectInstance[name]?.uncheckedCast()
  }

  return null
}

inline fun <reified C : Any, T> getStaticField(name: String): T? {
  return C::class.java.getStaticField(name)
}

@Suppress("UNCHECKED_CAST")
fun <T : Enum<out T>> Class<out T>.valueOf(value: String): T? {
  var valueOf: Method? = null
  return try {
    valueOf = getDeclaredMethod("valueOf", String::class.java)
    valueOf.invoke(null, value) as T
  } catch (e: Exception) {
    try {
      valueOf?.invoke(null, value.toUpperCase()) as? T?
    } catch (e: Exception) {
      null
    }
  }
}
