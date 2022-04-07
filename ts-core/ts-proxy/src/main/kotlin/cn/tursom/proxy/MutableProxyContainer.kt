package cn.tursom.proxy

interface MutableProxyContainer : ProxyContainer {
  fun addProxy(proxy: ProxyMethod): Int
  fun addAllProxy(proxy: Collection<ProxyMethod>?): Boolean
  fun removeProxy(proxy: ProxyMethod)
  fun removeProxy(index: Int)
}