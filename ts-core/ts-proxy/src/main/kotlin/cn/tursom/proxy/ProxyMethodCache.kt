package cn.tursom.proxy

import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

typealias ProxyMethodCacheFunction = (obj: Any?, method: Method, args: Array<out Any?>, proxy: MethodProxy) -> ProxyResult<*>

class ProxyMethodCache(
  private var lastModify: Long = 0,
  private var function: ProxyMethodCacheFunction = failed,
) {
  companion object {
    val failed: ProxyMethodCacheFunction = { _, _, _, _ -> ProxyResult.failed }
  }

  fun update(lastModify: Long, function: ProxyMethodCacheFunction = this.function) {
    this.lastModify = lastModify
    this.function = function
  }

  operator fun invoke(
    lastModify: Long,
    obj: Any?,
    method: Method,
    args: Array<out Any?>,
    proxy: MethodProxy,
  ): ProxyResult<*>? = if (lastModify != this.lastModify) {
    null
  } else {
    function(obj, method, args, proxy)
  }
}