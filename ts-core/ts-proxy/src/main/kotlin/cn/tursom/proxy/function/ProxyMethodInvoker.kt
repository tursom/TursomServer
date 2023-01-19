package cn.tursom.proxy.function

import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

internal class ProxyMethodInvoker(
  private val proxyMethod: ProxyMethod,
  private val next: ProxyMethodCacheFunction,
) : ProxyMethodCacheFunction {
  override fun invoke(
    obj: Any?,
    c: ProxyContainer,
    method: Method?,
    args: Array<out Any?>?,
    proxy: MethodProxy?,
  ): Any? {
    return proxyMethod.onProxyInvoke(obj, c, method, args, proxy, next)
  }
}
