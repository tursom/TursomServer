package cn.tursom.proxy

import cn.tursom.core.context.Context


class ListProxyContainer(
  private val proxyList: MutableCollection<ProxyMethod> = ArrayList(),
) : MutableProxyContainer {
  override var lastModify: Long = System.currentTimeMillis()
    private set
  override val context: Context = ProxyContainer.contextEnv.newContext()

  override fun addProxy(proxy: ProxyMethod) {
    lastModify = System.currentTimeMillis()
    proxyList.add(proxy)
  }

  override fun addAllProxy(proxy: Collection<ProxyMethod>?): Boolean {
    lastModify = System.currentTimeMillis()
    return proxyList.addAll(proxy!!)
  }

  override fun removeProxy(proxy: ProxyMethod) {
    lastModify = System.currentTimeMillis()
    proxyList.remove(proxy)
  }

  override fun iterator(): MutableIterator<ProxyMethod> {
    lastModify = System.currentTimeMillis()
    return proxyList.iterator()
  }
}