package cn.tursom.proxy

import cn.tursom.proxy.annotation.ForEachProxy
import cn.tursom.proxy.annotation.ForFirstProxy
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

interface ProxyMethod {
  @Throws(Throwable::class)
  fun onProxy(obj: Any?, method: Method, args: Array<out Any?>, proxy: MethodProxy): ProxyResult<*>? {
    val selfMethod: Method
    val handlerCacheMap = getHandlerCacheMap(javaClass)
    val methodResult = handlerCacheMap[method]
    if (methodResult != null) {
      return if (methodResult.success) {
        ProxyResult.of(methodResult.result(this, *args))
      } else {
        ProxyResult.failed<Any>()
      }
    }
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
      handlerCacheMap[method] = ProxyResult(selfMethod, true)
    } catch (e: Exception) {
      handlerCacheMap[method] = ProxyResult.failed()
      return ProxyResult.failed<Any>()
    }
    return ProxyResult.of<Any>(selfMethod(this, *args))
  }

  companion object {
    private val handlerCacheMapMap: MutableMap<Class<out ProxyMethod>, MutableMap<Method, ProxyResult<Method>>> =
      HashMap()

    fun getHandlerCacheMap(type: Class<out ProxyMethod>): MutableMap<Method, ProxyResult<Method>> {
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

    fun getProxyMethod(clazz: Class<*>, name: String, vararg parameterTypes: Class<*>): Method? {
      return try {
        clazz.getDeclaredMethod(name, *parameterTypes)
      } catch (e: NoSuchMethodException) {
        throw RuntimeException(e)
      }
    }
  }
}