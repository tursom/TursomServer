package cn.tursom.proxy.container

import cn.tursom.core.context.Context
import cn.tursom.proxy.Proxy
import cn.tursom.proxy.function.ProxyMethod
import net.sf.cglib.proxy.Factory


class ListProxyContainer(
  private val proxyList: MutableCollection<Any> = ArrayList(),
  override val nonProxyClasses: MutableSet<Class<*>> = HashSet(listOf(Any::class.java)),
) : MutableProxyContainer {
  override lateinit var target: Any
  override val ctx: Context = ProxyContainer.ctxEnv.newContext()

  private fun clearCache() {
    ctx[ProxyMethodCache.ctxKey].clear()

    Proxy.clearCallbackCache(target as Factory, this)
  }

  override fun addProxy(proxy: Any) {
    clearCache()

    proxyList.add(proxy)
    if (proxy is ProxyMethod) {
      proxy.onProxyAdded(this)
    }

    clearCache()
  }

  override fun addAllProxy(proxy: Collection<Any>?) {
    clearCache()

    if (proxyList.addAll(proxy!!)) proxy.forEach {
      if (it !is ProxyMethod) return@forEach
      it.onProxyAdded(this)
    }

    clearCache()
  }

  override fun removeProxy(proxy: Any) {
    clearCache()

    if (proxyList.remove(proxy) && proxy is ProxyMethod) {
      proxy.onProxyRemoved(this)
    }

    clearCache()
  }

  override fun iterator(): Iterator<Any> {
    return proxyList.iterator()
  }
}
