package cn.tursom.proxy


class ListProxy<T : ProxyMethod> : MutableProxy<T> {
  private val proxyList: MutableList<T> = ArrayList()

  override fun addProxy(proxy: T): Int {
    proxyList.add(proxy)
    return proxyList.size - 1
  }

  override fun addAllProxy(proxy: Collection<T>?): Boolean {
    return proxyList.addAll(proxy!!)
  }

  override fun removeProxy(proxy: T) {
    proxyList.remove(proxy)
  }

  override fun removeProxy(index: Int) {
    proxyList.removeAt(index)
  }

  override fun iterator(): MutableIterator<T> {
    return proxyList.iterator()
  }
}