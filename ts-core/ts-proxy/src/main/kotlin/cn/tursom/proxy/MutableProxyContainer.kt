package cn.tursom.proxy

interface MutableProxyContainer : ProxyContainer {
  fun addProxy(proxy: ProxyMethod)
  fun addAllProxy(proxy: Collection<ProxyMethod>?): Boolean
  fun removeProxy(proxy: ProxyMethod)
}