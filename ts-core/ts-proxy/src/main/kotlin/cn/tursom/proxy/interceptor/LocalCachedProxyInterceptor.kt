package cn.tursom.proxy.interceptor

import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCache
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import net.sf.cglib.proxy.Factory
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class LocalCachedProxyInterceptor(
  container: ProxyContainer,
  val target: Factory,
) : ProxyInterceptor(container), CachedMethodInterceptor {
  private var cache: ProxyMethodCacheFunction? = null
  private val c: ProxyContainer = container

  override fun clearCache() {
    cache = null
  }

  override fun intercept(obj: Any?, method: Method?, args: Array<out Any?>?, proxy: MethodProxy?): Any? {
    var cache = this.cache

    if (cache != null) {
      return cache(target, c, method, args, proxy)
    }

    proxy!!

    val methodCache = c.ctx[ProxyMethodCache.ctxKey]
    this.cache = methodCache[proxy]
    cache = this.cache

    if (cache != null) {
      return cache(target, c, method, args, proxy)
    }

    return super.intercept(target, method, args, proxy)
  }
}
