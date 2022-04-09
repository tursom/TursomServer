package cn.tursom.proxy

import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class ProxyInterceptor(
  val container: ProxyContainer = ListProxyContainer(),
  // disable call super can save 20% time
  private val supportCallSuper: Boolean = true,
) : MethodInterceptor {
  override fun intercept(obj: Any, method: Method, args: Array<out Any?>, proxy: MethodProxy): Any? {
    if (supportCallSuper && Proxy.callSuper.get()) {
      Proxy.callSuper.remove()
      return proxy.invokeSuper(obj, args)
    }

    val result = ProxyRunner.onProxy(obj, container, method, args, proxy)
    return if (result.success) {
      result.result
    } else {
      proxy.invokeSuper(obj, args)
    }
  }
}
