package cn.tursom.proxy


class ListProxyContainer : MutableProxyContainer {
  private val proxyList: MutableList<ProxyMethod> = ArrayList()

  override fun addProxy(proxy: ProxyMethod): Int {
    proxyList.add(proxy)
    return proxyList.size - 1
  }

  override fun addAllProxy(proxy: Collection<ProxyMethod>?): Boolean {
    return proxyList.addAll(proxy!!)
  }

  override fun removeProxy(proxy: ProxyMethod) {
    proxyList.remove(proxy)
  }

  override fun removeProxy(index: Int) {
    proxyList.removeAt(index)
  }

  override fun iterator(): MutableIterator<ProxyMethod> {
    return proxyList.iterator()
  }
}