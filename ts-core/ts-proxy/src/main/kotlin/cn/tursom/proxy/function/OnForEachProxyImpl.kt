package cn.tursom.proxy.function

import cn.tursom.proxy.annotation.ForEachProxy
import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCache
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class OnForEachProxyImpl(
  forEachProxy: ForEachProxy,
) : ProxyMethodCacheFunction {
  companion object {
    private val emptyProxyList = ArrayList<ProxyMethodCacheFunction>()
  }

  private val classes: Collection<Class<*>> = when (forEachProxy.value.size) {
    0 -> emptyList()
    1 -> listOf(forEachProxy.value[0].java)
    else -> forEachProxy.value.asSequence().map { it.java }.toSet()
  }

  private val cache: Boolean = forEachProxy.cache

  override fun invoke(
    o: Any?,
    c: ProxyContainer,
    m: Method?,
    a: Array<out Any?>?,
    proxy: MethodProxy?,
  ): Any? {
    m!!

    val proxyList = if (cache) ArrayList() else emptyProxyList
    c.forEach { p ->
      if (classes.isEmpty() || classes.any { c: Class<*> -> c.isInstance(p) }) {
        val handler = ProxyMethod.getHandler(p, m) ?: return@forEach
        handler(o, c, m, a, proxy)
        if (cache) {
          proxyList.add(handler)
        }
      }
    }

    proxy!!

    if (cache) {
      c.ctx[ProxyMethodCache.ctxKey].update(
        o!!, c, m, proxy,
        CachedOnForEachProxyImpl(proxyList),
      )
    }
    return proxy.invokeSuper(o, a)
  }
}