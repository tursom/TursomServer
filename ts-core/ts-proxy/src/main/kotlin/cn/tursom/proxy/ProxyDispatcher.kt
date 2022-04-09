package cn.tursom.proxy

import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

object ProxyDispatcher : MethodInterceptor {
  override fun intercept(obj: Any, method: Method, args: Array<out Any>, proxy: MethodProxy): Any {
    Proxy.injectCallback(obj)
    return proxy(obj, args)
  }
}