package cn.tursom.proxy.container

interface MutableProxyContainer : ProxyContainer {
  override var target: Any
  override val nonProxyClasses: MutableSet<Class<*>>

  fun addProxy(proxy: Any)
  fun addAllProxy(proxy: Collection<Any>?)
  fun removeProxy(proxy: Any)
}