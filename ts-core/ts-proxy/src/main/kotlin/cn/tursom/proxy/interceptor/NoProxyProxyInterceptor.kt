package cn.tursom.proxy.interceptor

import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class NoProxyProxyInterceptor(
  private val target: Any,
) : MethodInterceptor {
  override fun intercept(obj: Any?, method: Method?, args: Array<out Any>?, proxy: MethodProxy): Any? {
    return proxy.invokeSuper(target, args)
  }
}