package cn.tursom.proxy.function

import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class CachedOnForEachProxyImpl(
  private val proxyList: List<ProxyMethodCacheFunction>,
) : ProxyMethodCacheFunction {
  override fun invoke(
    o: Any?,
    c: ProxyContainer,
    m: Method?,
    a: Array<out Any?>?,
    proxy: MethodProxy?,
  ): Any? {
    proxyList.forEach { p ->
      p(o, c, m, a, proxy)
    }

    return proxy!!.invokeSuper(o, a)
  }
}