package cn.tursom.proxy.function

import cn.tursom.proxy.annotation.ForEachProxy
import cn.tursom.proxy.annotation.ForFirstProxy
import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import cn.tursom.reflect.asm.ReflectAsmInvoker
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

internal class JvmReflectProxyMethodInvoker(
  private val self: Any,
  private val method: Method,
) : ProxyMethodCacheFunction {
  companion object {
    operator fun get(
      proxy: Any,
      method: Method,
    ): JvmReflectProxyMethodInvoker? {
      var invoker: JvmReflectProxyMethodInvoker? = null

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

        invoker = JvmReflectProxyMethodInvoker(proxy, method)
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
    return ReflectAsmInvoker.invoke(this.method, self, args)
  }
}
