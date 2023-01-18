package cn.tursom.proxy

import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

typealias ProxyMethodCacheFunction = (
  obj: Any,
  c: ProxyContainer,
  method: Method,
  args: Array<out Any?>,
  proxy: MethodProxy
) -> ProxyResult<*>

class ProxyMethodCache(
  private var lastModify: Long = 0,
) {
  companion object {
    val failed: ProxyMethodCacheFunction = { _, _, _, _, _ -> ProxyResult.failed }
  }

  private val functionMap = ConcurrentHashMap<MethodProxy, ProxyMethodCacheFunction>()

  fun update(lastModify: Long, proxy: MethodProxy, function: ProxyMethodCacheFunction = functionMap[proxy] ?: failed) {
    if (this.lastModify != lastModify) {
      functionMap.clear()
    }

    this.lastModify = lastModify
    functionMap[proxy] = function
  }

  operator fun invoke(
    lastModify: Long,
    obj: Any,
    c: ProxyContainer,
    method: Method,
    args: Array<out Any?>,
    proxy: MethodProxy,
  ): ProxyResult<*>? = if (lastModify != this.lastModify) {
    null
  } else {
    functionMap[proxy]?.invoke(obj, c, method, args, proxy)
  }
}