package cn.tursom.proxy.container

interface MutableProxyContainer : ProxyContainer {
  override var target: Any

  fun addProxy(proxy: Any)
  fun addAllProxy(proxy: Collection<Any>?)
  fun removeProxy(proxy: Any)
}