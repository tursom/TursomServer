package cn.tursom.proxy.function

import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

interface ProxyMethod {
  fun onProxyAdded(container: ProxyContainer) {
    // NO-OP
  }

  fun onProxyRemoved(container: ProxyContainer) {
    // NO-OP
  }

  fun onProxyInvoke(
    o: Any?,
    c: ProxyContainer,
    m: Method?,
    a: Array<out Any?>?,
    proxy: MethodProxy?,
    next: ProxyMethodCacheFunction,
  ): Any? {
    return next(o, c, m, a, proxy)
  }

  companion object {
    fun getHandler(proxy: Any, method: Method): ProxyMethodCacheFunction? {
      var handler = getReflectHandler(proxy, method) ?: return null

      if (proxy is ProxyMethod) {
        handler = ProxyMethodInvoker(proxy, handler)
      }

      return handler
    }

    private fun getReflectHandler(proxy: Any, method: Method): ProxyMethodCacheFunction? {
      val reflectAsmHandler = ReflectASMProxyMethodInvoker[proxy, method]
      if (reflectAsmHandler != null) {
        return reflectAsmHandler
      }

      val javaReflectHandler = JavaReflectProxyMethodInvoker[proxy, method]
      if (javaReflectHandler != null) {
        return javaReflectHandler
      }

      return null
    }
  }
}
