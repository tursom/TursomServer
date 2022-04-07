@file:Suppress("unused")

package cn.tursom.reflect

import cn.tursom.core.uncheckedCast
import java.lang.reflect.Method

inline fun <R> Class<*>.getDeclaredMethod(
  returnType: Class<*>,
  getMethod: Class<*>.() -> Method?,
  onGetMethod: (Method) -> Unit = {},
  staticReturn: (method: Method) -> R,
): R? {
  val method = this.getDeclaredMethod(returnType, false, getMethod)
  if (method != null) {
    onGetMethod(method)
    return staticReturn(method)
  }
  return null
}

inline fun <reified R : Any> Any.getMethod(
  name: String,
  clazz: Class<*> = javaClass,
  returnType: Class<R> = R::class.java,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name)
  },
): (() -> R)? = clazz.getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { method(this).uncheckedCast() }
} ?: clazz.getStaticDeclaredMethod(name, returnType, onGetMethod, getMethod)

inline fun <reified R : Any, reified T1> Any.getMethod1(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  clazz: Class<*> = javaClass,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type)
  },
): ((T1) -> R)? = clazz.getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { method(this, it).uncheckedCast() }
} ?: clazz.getStaticDeclaredMethod1(name, returnType, arg1Type, onGetMethod, getMethod)

inline fun <reified R : Any, reified T1, reified T2> Any.getMethod2(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  arg2Type: Class<T2> = T2::class.java,
  clazz: Class<*> = javaClass,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type)
  },
): ((T1, T2) -> R)? = clazz.getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { v1, v2 -> method(this, v1, v2).uncheckedCast() }
} ?: clazz.getStaticDeclaredMethod2(name, returnType, arg1Type, arg2Type, onGetMethod, getMethod)

inline fun <reified R : Any, reified T1, reified T2, reified T3> Any.getMethod3(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  arg2Type: Class<T2> = T2::class.java,
  arg3Type: Class<T3> = T3::class.java,
  clazz: Class<*> = javaClass,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type)
  },
): ((T1, T2, T3) -> R)? = clazz.getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { v1, v2, v3 -> method(this, v1, v2, v3).uncheckedCast() }
} ?: clazz.getStaticDeclaredMethod3(name, returnType, arg1Type, arg2Type, arg3Type, onGetMethod, getMethod)

inline fun <reified R : Any, reified T1, reified T2, reified T3, reified T4> Any.getMethod4(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  arg2Type: Class<T2> = T2::class.java,
  arg3Type: Class<T3> = T3::class.java,
  arg4Type: Class<T4> = T4::class.java,
  clazz: Class<*> = javaClass,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type, arg4Type)
  },
): ((T1, T2, T3, T4) -> R)? = clazz.getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { v1, v2, v3, v4 -> method(this, v1, v2, v3, v4).uncheckedCast() }
} ?: clazz.getStaticDeclaredMethod4(name, returnType, arg1Type, arg2Type, arg3Type, arg4Type, onGetMethod, getMethod)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Any.getMethod5(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  arg2Type: Class<T2> = T2::class.java,
  arg3Type: Class<T3> = T3::class.java,
  arg4Type: Class<T4> = T4::class.java,
  arg5Type: Class<T5> = T5::class.java,
  clazz: Class<*> = javaClass,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type, arg4Type, arg5Type)
  },
): ((T1, T2, T3, T4, T5) -> R)? = clazz.getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { v1, v2, v3, v4, v5 -> method(this, v1, v2, v3, v4, v5).uncheckedCast() }
} ?: clazz.getStaticDeclaredMethod5(
  name,
  returnType, arg1Type, arg2Type, arg3Type, arg4Type, arg5Type,
  onGetMethod, getMethod
)

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Any.getMethod6(
  name: String,
  returnType: Class<R> = R::class.java,
  arg1Type: Class<T1> = T1::class.java,
  arg2Type: Class<T2> = T2::class.java,
  arg3Type: Class<T3> = T3::class.java,
  arg4Type: Class<T4> = T4::class.java,
  arg5Type: Class<T5> = T5::class.java,
  arg6Type: Class<T6> = T6::class.java,
  clazz: Class<*> = javaClass,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type, arg4Type, arg5Type, arg6Type)
  },
): ((T1, T2, T3, T4, T5, T6) -> R)? = clazz.getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { v1, v2, v3, v4, v5, v6 -> method(this, v1, v2, v3, v4, v5, v6).uncheckedCast() }
} ?: clazz.getStaticDeclaredMethod6(
  name,
  returnType, arg1Type, arg2Type, arg3Type, arg4Type, arg5Type, arg6Type,
  onGetMethod, getMethod
)

