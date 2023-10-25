@file:Suppress("unused")

package cn.tursom.core.reflect

import cn.tursom.core.util.allMethodsSequence
import cn.tursom.core.util.companionObjectInstanceOrNull
import cn.tursom.core.util.uncheckedCast
import com.ddbes.kotlin.isStatic
import java.lang.reflect.Method

private var lastGetMethodThreadLocal = ThreadLocal<Method?>()
private val lastGetMethod get() = lastGetMethodThreadLocal.get()

inline fun <reified T> getType() = T::class.java

infix fun Class<*>.canCast(target: Class<*>): Boolean {
  if (target == Any::class.java || this == target) return true

  if ((this == Void.TYPE || this == Void::class.java) &&
    (target == Unit::class.java || target == Void.TYPE || target == Void::class.java)
  ) return true

  if ((this == Byte::class.java || this == getType<Byte>()) &&
    (target == Byte::class.java || target == Byte::class.java)
  ) return true
  if ((this == Short::class.java || this == getType<Short>()) &&
    (target == Short::class.java || target == Short::class.java)
  ) return true
  if ((this == Int::class.java || this == getType<Int>()) &&
    (target == Int::class.java || target == getType<Int>())
  ) return true
  if ((this == Long::class.java || this == getType<Long>()) &&
    (target == Long::class.java || target == getType<Long>())
  ) return true

  if ((this == Float::class.java || this == getType<Float>()) &&
    (target == Float::class.java || target == getType<Float>())
  ) return true
  if ((this == Double::class.java || this == getType<Double>()) &&
    (target == Double::class.java || target == getType<Double>())
  ) return true

  if ((this == Boolean::class.java || this == getType<Boolean>()) &&
    (target == Boolean::class.java || target == getType<Boolean>())
  ) return true

  if ((this == Char::class.java || this == getType<Char>()) &&
    (target == Char::class.java || target == getType<Char>())
  ) return true

  return target.isAssignableFrom(this)
}

private fun Method.match(name: String, vararg type: Class<*>): Boolean {
  if (this.name != name) return false
  if (this.parameterTypes.size != type.size) return false
  this.parameterTypes.forEachIndexed { index, clazz ->
    if (!(type[index] canCast clazz)) return false
  }
  return true
}

fun Class<*>.getFirstMatchMethod(
  name: String,
  vararg type: Class<*>,
): Method? {
  lastGetMethodThreadLocal.remove()
  allMethodsSequence.forEach { method ->
    if (method.match(name, *type)) {
      lastGetMethodThreadLocal.set(method)
      return method
    }
  }
  return null
}

fun Class<*>.getMethodFully(
  name: String,
  vararg type: Class<*>,
): Method? {
  lastGetMethodThreadLocal.remove()
  val scanMethod = scanMethod(name, *type)
  return (scanMethod.firstOrNull { method ->
    method.parameterTypes.forEachIndexed { index, clazz ->
      if (type[index] != clazz) return@firstOrNull false
    }
    true
  } ?: scanMethod.firstOrNull()).also {
    lastGetMethodThreadLocal.set(it)
  }
}

fun Class<*>.scanMethod(
  name: String,
  vararg type: Class<*>,
): List<Method> {
  @OptIn(ExperimentalStdlibApi::class)
  return buildList {
    allMethodsSequence.forEach { method ->
      if (method.match(name, *type)) add(method)
    }
  }
}

inline fun Class<*>.getStaticDeclaredMethod(
  returnType: Class<*>,
  isStatic: Boolean = true,
  getMethod: Class<*>.() -> Method?,
) = try {
  val method = getMethod()
  method?.isAccessible = true
  if (method != null && method.isStatic() == isStatic && method.returnType canCast returnType) method
  else null
} catch (e: Exception) {
  null
}

inline fun <R> Class<*>.getStaticDeclaredMethod(
  returnType: Class<*>,
  getMethod: Class<*>.() -> Method?,
  staticReturn: (method: Method, companionObjectInstance: Any?) -> R,
): R? {
  val method = getStaticDeclaredMethod(returnType, true, getMethod)
  if (method != null) return staticReturn(method, null)

  val companionObjectInstance = kotlin.companionObjectInstanceOrNull ?: return null
  val companionObjectClazz = companionObjectInstance.javaClass
  val companionMethod = companionObjectClazz.getStaticDeclaredMethod(returnType, false, getMethod)
  if (companionMethod != null) return staticReturn(companionMethod, companionObjectInstance)

  return null
}

inline fun <reified R : Any> Class<*>.getStaticDeclaredMethod(
  name: String,
  returnType: Class<R> = R::class.java,
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name)
  },
): (() -> R)? = getStaticDeclaredMethod(
  returnType, getMethod,
  { method, companion -> { method(companion).uncheckedCast() } }
)

inline fun <reified R, reified T1> Class<*>.getStaticDeclaredMethod1(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type)
  },
): ((T1) -> R)? = getStaticDeclaredMethod(
  returnType, getMethod,
  { method, companion -> { v1 -> method(companion, v1).uncheckedCast() } }
)

inline fun <reified R, reified T1, reified T2> Class<*>.getStaticDeclaredMethod2(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  arg2Type: Class<T2> = T2::class.java,
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type)
  },
): ((T1, T2) -> R)? = getStaticDeclaredMethod(
  returnType, getMethod,
  { method, companion -> { v1, v2 -> method(companion, v1, v2).uncheckedCast() } }
)

inline fun <reified R, reified T1, reified T2, reified T3> Class<*>.getStaticDeclaredMethod3(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  arg2Type: Class<T2> = T2::class.java,
  arg3Type: Class<T3> = T3::class.java,
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type)
  },
): ((T1, T2, T3) -> R)? = getStaticDeclaredMethod(
  returnType, getMethod,
  { method, companion -> { v1, v2, v3 -> method(companion, v1, v2, v3).uncheckedCast() } }
)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4> Class<*>.getStaticDeclaredMethod4(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  arg2Type: Class<T2> = T2::class.java,
  arg3Type: Class<T3> = T3::class.java,
  arg4Type: Class<T4> = T4::class.java,
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type, arg4Type)
  },
): ((T1, T2, T3, T4) -> R)? = getStaticDeclaredMethod(
  returnType, getMethod,
  { method, companion -> { v1, v2, v3, v4 -> method(companion, v1, v2, v3, v4).uncheckedCast() } }
)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Class<*>.getStaticDeclaredMethod5(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  arg2Type: Class<T2> = T2::class.java,
  arg3Type: Class<T3> = T3::class.java,
  arg4Type: Class<T4> = T4::class.java,
  arg5Type: Class<T5> = T5::class.java,
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type, arg4Type, arg5Type)
  },
): ((T1, T2, T3, T4, T5) -> R)? = getStaticDeclaredMethod(
  returnType, getMethod,
  { method, companion -> { v1, v2, v3, v4, v5 -> method(companion, v1, v2, v3, v4, v5).uncheckedCast() } }
)


