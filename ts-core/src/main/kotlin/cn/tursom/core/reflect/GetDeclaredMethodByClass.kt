@file:Suppress("unused")

package cn.tursom.core.reflect

import cn.tursom.core.uncheckedCast
import java.lang.reflect.Method

inline fun <This, reified R> Class<*>.getMethod(
  name: String,
  returnType: Class<out R> = R::class.java,
  getMethod: Class<*>.() -> Method? = { getMethodFully(name) },
): (This.() -> R)? = getStaticDeclaredMethod(returnType, getMethod) { method ->
  { method(this).uncheckedCast() }
}

inline fun <This, reified R, reified T1> Class<*>.getMethod1(
  name: String,
  returnType: Class<out R> = R::class.java,
  arg1Type: Class<in T1> = T1::class.java,
  getMethod: Class<*>.() -> Method? = { getMethodFully(name, arg1Type) },
): (This.(T1) -> R)? = getStaticDeclaredMethod(returnType, getMethod) { method ->
  { method(this, it).uncheckedCast() }
}

inline fun <This, reified R, reified T1, reified T2> Class<*>.getMethod2(
  name: String,
  returnType: Class<out R> = R::class.java,
  arg1Type: Class<in T1> = T1::class.java,
  arg2Type: Class<in T2> = T2::class.java,
  getMethod: Class<*>.() -> Method? = { getMethodFully(name, arg1Type, arg2Type) },
): (This.(T1, T2) -> R)? = getStaticDeclaredMethod(returnType, getMethod) { method ->
  { v1, v2 -> method(this, v1, v2).uncheckedCast() }
}

inline fun <This, reified R, reified T1, reified T2, reified T3> Class<*>.getMethod3(
  name: String,
  returnType: Class<out R> = R::class.java,
  arg1Type: Class<in T1> = T1::class.java,
  arg2Type: Class<in T2> = T2::class.java,
  arg3Type: Class<in T3> = T3::class.java,
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type)
  },
): (This.(T1, T2, T3) -> R)? = getStaticDeclaredMethod(returnType, getMethod) { method ->
  { v1, v2, v3 -> method(this, v1, v2, v3).uncheckedCast() }
}

inline fun <This, reified R, reified T1, reified T2, reified T3, reified T4> Class<*>.getMethod4(
  name: String,
  returnType: Class<out R> = R::class.java,
  arg1Type: Class<in T1> = T1::class.java,
  arg2Type: Class<in T2> = T2::class.java,
  arg3Type: Class<in T3> = T3::class.java,
  arg4Type: Class<in T4> = T4::class.java,
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type, arg4Type)
  },
): (This.(T1, T2, T3, T4) -> R)? = getStaticDeclaredMethod(returnType, getMethod) { method ->
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
  getMethod: Class<*>.() -> Method? = {
    getMethodFully(name, arg1Type, arg2Type, arg3Type, arg4Type)
  },
): (This.(T1, T2, T3, T4, T5) -> R)? = getStaticDeclaredMethod(returnType, getMethod) { method ->
  { v1, v2, v3, v4, v5 -> method(this, v1, v2, v3, v4, v5).uncheckedCast() }
}

