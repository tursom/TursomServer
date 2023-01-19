package cn.tursom.proxy.container

import cn.tursom.proxy.util.IntMap
import net.sf.cglib.proxy.MethodProxy

class ProxyMethodCache {
  companion object {
    val ctxKey = ProxyContainer.ctxEnv.newKey<ProxyMethodCache>()
      .withDefault { ProxyMethodCache() }
  }

  //private val functionMap = HashMap<Int, ProxyMethodCacheFunction>()
  private val functionMap = IntMap<ProxyMethodCacheFunction>()

  fun clear() {
    synchronized(functionMap) {
      functionMap.clear()
    }
  }

  fun update(proxy: MethodProxy, function: ProxyMethodCacheFunction) {
    synchronized(this) {
      functionMap[proxy.superIndex] = function
    }
  }

  operator fun get(proxy: MethodProxy) = functionMap[proxy.superIndex]
}
