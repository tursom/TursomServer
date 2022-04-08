package cn.tursom.proxy

import cn.tursom.core.uncheckedCast
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.Factory
import java.util.concurrent.ConcurrentHashMap

object Proxy {
  val enhancerMap = ConcurrentHashMap<Class<*>, Enhancer>()
  private val cache = ConcurrentHashMap<Class<*>, Class<*>>()

  private fun <T> getTarget(clazz: Class<T>): Class<T> = cache.computeIfAbsent(clazz) {
    val enhancer = Enhancer()
    enhancerMap[clazz] = enhancer
    enhancer.setSuperclass(clazz)
    enhancer.setCallbackType(ProxyInterceptor::class.java)
    enhancer.setCallbackFilter { 0 }
    enhancer.createClass()
  }.uncheckedCast()

  operator fun <T> get(
    clazz: Class<T>,
    builder: (Class<T>) -> T,
  ): Pair<T, MutableProxyContainer> {
    val target = getTarget(clazz)
    val container = ListProxyContainer()
    val obj = builder(target)
    obj as Factory
    obj.setCallback(0, ProxyInterceptor(container))
    return obj to container
  }

  inline fun <reified T> get() = get(T::class.java)
  inline fun <reified T> get(
    argumentTypes: Array<out Class<*>>,
    arguments: Array<out Any?>,
  ) = get(T::class.java, argumentTypes, arguments)

  operator fun <T> get(clazz: Class<T>) = get(clazz, Class<T>::newInstance)

  operator fun <T> get(
    clazz: Class<T>,
    argumentTypes: Array<out Class<*>>,
    arguments: Array<out Any?>,
  ) = get(clazz) {
    it.getConstructor(*argumentTypes).newInstance(*arguments)
  }
}