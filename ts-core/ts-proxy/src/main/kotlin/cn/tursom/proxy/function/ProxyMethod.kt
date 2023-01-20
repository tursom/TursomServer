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

  fun onGet(f: ProxyMethodCacheFunction, method: Method): ProxyMethodCacheFunction = f

  /**
   * disabled on default.
   *
   * override onCached to enable it
   */
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

  /**
   * 当有缓存更新时被调用
   *
   * 你可以以此替换缓存方法
   */
  fun onProxyHandlerCacheUpdate(
    f: ProxyMethodCacheFunction,
    obj: Any,
    container: ProxyContainer,
    method: Method,
    proxy: MethodProxy,
  ) = f

  companion object {
    fun getHandler(proxy: Any, method: Method): ProxyMethodCacheFunction? {
      var handler = getReflectHandler(proxy, method) ?: return null

      if (proxy is ProxyMethod) {
        handler = proxy.onGet(handler, method)
      }

      return handler
    }

    private fun getReflectHandler(proxy: Any, method: Method): ProxyMethodCacheFunction? {
      val reflectAsmHandler = ReflectASMProxyMethodInvoker[proxy, method]
      if (reflectAsmHandler != null) {
        return reflectAsmHandler.toJava()
      }

      val javaReflectHandler = JvmReflectProxyMethodInvoker[proxy, method]
      if (javaReflectHandler != null) {
        return javaReflectHandler
      }

      return null
    }
  }
}
