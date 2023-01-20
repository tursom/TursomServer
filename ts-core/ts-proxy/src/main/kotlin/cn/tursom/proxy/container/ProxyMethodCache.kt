package cn.tursom.proxy.container

import cn.tursom.proxy.function.ProxyMethod
import cn.tursom.proxy.util.IntMap
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

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

  fun update(
    obj: Any,
    container: ProxyContainer,
    method: Method,
    proxy: MethodProxy,
    function: ProxyMethodCacheFunction,
  ) {
    var handler = function
    container.forEach {
      if (it !is ProxyMethod) return@forEach

      handler = it.onProxyHandlerCacheUpdate(handler, obj, container, method, proxy)
    }

    synchronized(this) {
      functionMap[proxy.superIndex] = handler
    }
  }

  operator fun get(proxy: MethodProxy) = functionMap[proxy.superIndex]
}
