package cn.tursom.proxy

import cn.tursom.proxy.annotation.ForEachProxy
import cn.tursom.proxy.annotation.ForFirstProxy
import cn.tursom.reflect.asm.ReflectAsmUtils
import com.esotericsoftware.reflectasm.MethodAccess
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

internal typealias ProxyMethodHandler = (proxy: ProxyMethod, args: Array<out Any?>) -> ProxyResult<*>

interface ProxyMethod {
  @Throws(Throwable::class)
  fun onProxy(
    ignored: Any,
    c: ProxyContainer,
    method: Method,
    args: Array<out Any?>,
    proxy: MethodProxy
  ): ProxyResult<*> {
    val handlerCacheMap = getHandlerCacheMap(javaClass)
    val handlerCache = handlerCacheMap[method]
    if (handlerCache != null) {
      return if (handlerCache.success) {
        handlerCache.result(this, args)
      } else {
        ProxyResult.failed<Any>()
      }
    }

    val reflectAsmHandler = ReflectASMProxyMethodInvoker[this, method, handlerCacheMap]
    if (reflectAsmHandler != null) {
      return reflectAsmHandler(this, args)
    }

    val javaReflectHandler = JavaReflectProxyMethodInvoker[this, method, handlerCacheMap]
    if (javaReflectHandler != null) {
      javaReflectHandler(this, args)
    }

    handlerCacheMap[method] = ProxyResult.failed()
    return ProxyResult.failed<Any>()
  }

  companion object {
    private val handlerCacheMapMap: MutableMap<
      Class<out ProxyMethod>,
      MutableMap<Method, ProxyResult<ProxyMethodHandler>>
      > = HashMap()

    fun getHandlerCacheMap(type: Class<out ProxyMethod>): MutableMap<Method, ProxyResult<ProxyMethodHandler>> {
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

private class ReflectASMProxyMethodInvoker(
  val methodAccess: MethodAccess,
  val index: Int,
) : ProxyMethodHandler {
  companion object {
    private val fastInvoker: MethodAccess.(Any, Int, Array<out Any?>) -> Any? = MethodAccess::invoke

    operator fun get(
      proxy: ProxyMethod,
      method: Method,
      handlerCacheMap: MutableMap<Method, ProxyResult<ProxyMethodHandler>>
    ): ProxyMethodHandler? {
      val reflectAsmMethod = try {
        ReflectAsmUtils.getMethod(
          proxy.javaClass,
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
        val invoker = ReflectASMProxyMethodInvoker(methodAccess, index)
        handlerCacheMap[method] = ProxyResult.of(invoker)
        return invoker
      }

      return null
    }
  }

  override fun invoke(proxy: ProxyMethod, args: Array<out Any?>): ProxyResult<*> {
    return ProxyResult.of(fastInvoker(methodAccess, proxy, index, args))
  }
}

private class JavaReflectProxyMethodInvoker(
  val method: Method,
) : ProxyMethodHandler {
  companion object {
    private val fastInvoker: Method.(Any, Array<out Any?>) -> Any? = Method::invoke

    operator fun get(
      proxy: ProxyMethod,
      method: Method,
      handlerCacheMap: MutableMap<Method, ProxyResult<ProxyMethodHandler>>
    ): ProxyMethodHandler? {
      var invoker: ProxyMethodHandler? = null

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
        selfMethod = proxy.javaClass.getMethod(methodName, *method.parameterTypes)
        selfMethod.isAccessible = true

        invoker = JavaReflectProxyMethodInvoker(method)

        handlerCacheMap[method] = ProxyResult(invoker, true)
      } catch (_: Exception) {
      }

      return invoker
    }
  }

  override fun invoke(proxy: ProxyMethod, args: Array<out Any?>): ProxyResult<*> {
    return ProxyResult.of(fastInvoker(method, proxy, args))
  }
}
