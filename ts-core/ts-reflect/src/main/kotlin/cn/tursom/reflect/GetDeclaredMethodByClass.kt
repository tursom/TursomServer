@file:Suppress("unused")

package cn.tursom.reflect

import cn.tursom.core.uncheckedCast
import java.lang.reflect.Method

inline fun <This, reified R> Class<*>.getMethod(
  name: String,
  returnType: Class<out R> = R::class.java,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name)
  },
): (This.() -> R)? = getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { method(this).uncheckedCast() }
}

inline fun <This, reified R, reified T1> Class<*>.getMethod1(
  name: String,
  returnType: Class<out R> = R::class.java,
  arg1Type: Class<in T1> = T1::class.java,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = { getMethodFully(name, arg1Type) },
): (This.(T1) -> R)? = getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { method(this, it).uncheckedCast() }
}

inline fun <This, reified R, reified T1, reified T2> Class<*>.getMethod2(
  name: String,
  returnType: Class<out R> = R::class.java,
  arg1Type: Class<in T1> = T1::class.java,
  arg2Type: Class<in T2> = T2::class.java,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = { getMethodFully(name, arg1Type, arg2Type) },
): (This.(T1, T2) -> R)? = getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { v1, v2 -> method(this, v1, v2).uncheckedCast() }
}

inline fun <This, reified R, reified T1, reified T2, reified T3> Class<*>.getMethod3(
  name: String,
  returnType: Class<out R> = R::class.java,
  arg1Type: Class<in T1> = T1::class.java,
  arg2Type: Class<in T2> = T2::class.java,
  arg3Type: Class<in T3> = T3::class.java,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type)
  },
): (This.(T1, T2, T3) -> R)? = getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { v1, v2, v3 -> method(this, v1, v2, v3).uncheckedCast() }
}

inline fun <This, reified R, reified T1, reified T2, reified T3, reified T4> Class<*>.getMethod4(
  name: String,
  returnType: Class<out R> = R::class.java,
  arg1Type: Class<in T1> = T1::class.java,
  arg2Type: Class<in T2> = T2::class.java,
  arg3Type: Class<in T3> = T3::class.java,
  arg4Type: Class<in T4> = T4::class.java,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type, arg4Type)
  },
): (This.(T1, T2, T3, T4) -> R)? = getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { v1, v2, v3, v4 -> method(this, v1, v2, v3, v4).uncheckedCast() }
}

inline fun <This, reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Class<*>.getMethod5(
  name: String,
  returnType: Class<out R> = R::class.java,
  arg1Type: Class<in T1> = T1::class.java,
  arg2Type: Class<in T2> = T2::class.java,
  arg3Type: Class<in T3> = T3::class.java,
  arg4Type: Class<in T4> = T4::class.java,
  arg5Type: Class<in T5> = T5::class.java,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type, arg4Type, arg5Type)
  },
): (This.(T1, T2, T3, T4, T5) -> R)? = getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { v1, v2, v3, v4, v5 -> method(this, v1, v2, v3, v4, v5).uncheckedCast() }
}

inline fun <This, reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Class<*>.getMethod6(
  name: String,
  returnType: Class<out R> = R::class.java,
  arg1Type: Class<in T1> = T1::class.java,
  arg2Type: Class<in T2> = T2::class.java,
  arg3Type: Class<in T3> = T3::class.java,
  arg4Type: Class<in T4> = T4::class.java,
  arg5Type: Class<in T5> = T5::class.java,
  arg6Type: Class<in T6> = T6::class.java,
  onGetMethod: (Method) -> Unit = {},
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type, arg4Type, arg5Type, arg6Type)
  },
): (This.(T1, T2, T3, T4, T5, T6) -> R)? = getDeclaredMethod(returnType, getMethod, onGetMethod) { method ->
  { v1, v2, v3, v4, v5, v6 -> method(this, v1, v2, v3, v4, v5, v6).uncheckedCast() }
}

