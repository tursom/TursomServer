package cn.tursom.proxy

interface MutableProxy<T : ProxyMethod> : Proxy<T> {
  fun addProxy(proxy: T): Int
  fun addAllProxy(proxy: Collection<T>?): Boolean
  fun removeProxy(proxy: T)
  fun removeProxy(index: Int)
}