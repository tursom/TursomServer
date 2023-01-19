package cn.tursom.proxy.interceptor

import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCache
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class LocalCachedProxyInterceptor(
  container: ProxyContainer,
  nonProxyClasses: MutableSet<Class<*>>,
  val target: Any,
) : ProxyInterceptor(container, nonProxyClasses) {
  private var cache: ProxyMethodCacheFunction? = null

  override fun intercept(obj: Any?, method: Method?, args: Array<out Any?>?, proxy: MethodProxy): Any? {
    var cache = this.cache

    if (cache != null) {
      return cache(target, container, method, args, proxy)
    }

    val methodCache = container.ctx[ProxyMethodCache.ctxKey]
    this.cache = methodCache[proxy]
    cache = this.cache

    if (cache != null) {
      return cache(target, container, method, args, proxy)
    }

    return super.intercept(target, method, args, proxy)
  }
}
