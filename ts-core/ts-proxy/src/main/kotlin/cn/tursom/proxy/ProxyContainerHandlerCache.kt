package cn.tursom.proxy

import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

object ProxyContainerHandlerCache {
  private val handlerMap: MutableMap<Method, (Any, Method, Array<out Any?>, MethodProxy) -> Proxy.Result<Any?>> =
    ConcurrentHashMap()
  val callSuper = { obj: Any, method: Method, args: Array<out Any?>, proxy: MethodProxy ->
    Proxy.of<Any?>(proxy.invokeSuper(obj, args))
  }
  val empty = { obj: Any, method: Method, args: Array<out Any?>, proxy: MethodProxy -> Proxy.failed<Any?>() }

  fun getHandler(method: Method): ((Any, Method, Array<out Any?>, MethodProxy) -> Proxy.Result<Any?>)? {
    return handlerMap[method]
  }

  fun setHandler(method: Method, onProxy: ((Any, Method, Array<out Any?>, MethodProxy) -> Proxy.Result<Any?>)?) {
    handlerMap[method] = onProxy ?: callSuper
  }
}