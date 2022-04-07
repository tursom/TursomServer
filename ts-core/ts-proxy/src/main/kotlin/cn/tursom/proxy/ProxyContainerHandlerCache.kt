package cn.tursom.proxy

import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

object ProxyContainerHandlerCache {
  private val handlerMap: MutableMap<Method, (Any, ProxyContainer, Method, Array<out Any?>, MethodProxy) -> ProxyResult<Any?>> =
    ConcurrentHashMap()
  val callSuper = { obj: Any, _: ProxyContainer, _: Method, args: Array<out Any?>, proxy: MethodProxy ->
    ProxyResult.of<Any?>(proxy.invokeSuper(obj, args))
  }
  val empty = { _: Any, _: ProxyContainer, _: Method, _: Array<out Any?>, _: MethodProxy -> ProxyResult.failed<Any?>() }

  fun getHandler(method: Method): ((Any, ProxyContainer, Method, Array<out Any?>, MethodProxy) -> ProxyResult<Any?>)? {
    return handlerMap[method]
  }

  fun setHandler(
    method: Method,
    onProxy: ((Any, ProxyContainer, Method, Array<out Any?>, MethodProxy) -> ProxyResult<Any?>)?,
  ) {
    handlerMap[method] = onProxy ?: callSuper
  }
}