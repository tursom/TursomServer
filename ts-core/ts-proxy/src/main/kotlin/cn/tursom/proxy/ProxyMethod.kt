package cn.tursom.proxy

import cn.tursom.proxy.annotation.ForEachProxy
import cn.tursom.proxy.annotation.ForFirstProxy
import cn.tursom.reflect.asm.ReflectAsmUtils
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

interface ProxyMethod {
  @Throws(Throwable::class)
  fun onProxy(obj: Any?, method: Method, args: Array<out Any?>, proxy: MethodProxy): ProxyResult<*> {
    val handlerCacheMap = getHandlerCacheMap(javaClass)
    val methodResult = handlerCacheMap[method]
    if (methodResult != null) {
      return if (methodResult.success) {
        ProxyResult.of(methodResult.result(this, args))
      } else {
        ProxyResult.failed<Any>()
      }
    }

    val reflectAsmMethod = try {
      ReflectAsmUtils.getMethod(
        javaClass,
        method.name,
        paramTypes = method.parameterTypes,
        returnType = method.returnType,
      )
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
    if (reflectAsmMethod != null) {
      val (methodAccess, index) = reflectAsmMethod
      handlerCacheMap[method] = ProxyResult({ p, a ->
        methodAccess.invoke(p, index, *a)
      }, true)
      return ProxyResult.of(methodAccess.invoke(this, index, *args))
    }

    val selfMethod: Method
    try {
      var methodName = method.name
      for (annotation in method.annotations) {
        if (annotation is ForEachProxy) {
          if (annotation.name.isNotEmpty()) {
            methodName = annotation.name
            break
          }
        } else if (annotation is ForFirstProxy) {
          if (annotation.name.isNotEmpty()) {
            methodName = annotation.name
            break
          }
        }
      }
      selfMethod = javaClass.getMethod(methodName, *method.parameterTypes)
      selfMethod.isAccessible = true
      handlerCacheMap[method] = ProxyResult({ p, a ->
        selfMethod(p, *a)
      }, true)
      return ProxyResult.of<Any>(selfMethod(this, *args))
    } catch (_: Exception) {
    }

    handlerCacheMap[method] = ProxyResult.failed()
    return ProxyResult.failed<Any>()
  }

  companion object {
    private val handlerCacheMapMap: MutableMap<
        Class<out ProxyMethod>,
        MutableMap<Method, ProxyResult<(proxy: ProxyMethod, args: Array<out Any?>) -> Any?>>> =
      HashMap()

    fun getHandlerCacheMap(type: Class<out ProxyMethod>): MutableMap<Method, ProxyResult<(proxy: ProxyMethod, args: Array<out Any?>) -> Any?>> {
      var handlerCacheMap = handlerCacheMapMap[type]
      if (handlerCacheMap == null) synchronized(handlerCacheMapMap) {
        handlerCacheMap = handlerCacheMapMap[type]
        if (handlerCacheMap == null) {
          handlerCacheMap = ConcurrentHashMap()
          handlerCacheMapMap[type] = handlerCacheMap!!
        }
      }
      return handlerCacheMap!!
    }
  }
}