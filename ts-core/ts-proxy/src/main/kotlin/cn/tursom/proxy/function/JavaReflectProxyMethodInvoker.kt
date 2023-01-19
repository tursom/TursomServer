package cn.tursom.proxy.function

import cn.tursom.proxy.annotation.ForEachProxy
import cn.tursom.proxy.annotation.ForFirstProxy
import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

internal class JavaReflectProxyMethodInvoker(
  private val self: Any,
  private val method: Method,
) : ProxyMethodCacheFunction {
  companion object {
    private val fastInvoker: Method.(Any?, Array<out Any?>?) -> Any? = Method::invoke

    operator fun get(
      proxy: Any,
      method: Method,
    ): JavaReflectProxyMethodInvoker? {
      var invoker: JavaReflectProxyMethodInvoker? = null

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

        invoker = JavaReflectProxyMethodInvoker(proxy, method)

        //handlerCacheMap[method] = ProxyResult(invoker, true)
      } catch (_: Exception) {
      }

      return invoker
    }
  }

  override fun invoke(
    obj: Any?,
    c: ProxyContainer,
    method: Method?,
    args: Array<out Any?>?,
    proxy: MethodProxy?,
  ): Any? {
    return fastInvoker(this.method, self, args)
  }
}
