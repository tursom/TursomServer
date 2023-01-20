package cn.tursom.proxy

import cn.tursom.core.allMethodsSequence
import cn.tursom.core.isPrivate
import cn.tursom.core.uncheckedCast
import cn.tursom.proxy.container.ListProxyContainer
import cn.tursom.proxy.container.MutableProxyContainer
import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.function.ProxyMethod
import cn.tursom.proxy.interceptor.CachedMethodInterceptor
import cn.tursom.proxy.interceptor.LocalCachedProxyInterceptor
import cn.tursom.proxy.interceptor.ProxyInterceptor
import cn.tursom.reflect.final
import net.sf.cglib.proxy.*
import java.util.concurrent.ConcurrentHashMap

object Proxy {
  var defaultContainer: () -> MutableProxyContainer = { ListProxyContainer() }
  private val cache = ConcurrentHashMap<Class<*>, Class<*>>()
  val directAccessorKey = ProxyContainer.ctxEnv.newKey<Any>()

  private val methodProxyFieldSignature = MethodProxy::class.java.getDeclaredField("sig1").also {
    it.isAccessible = true
    it.final = false
  }

  fun getContainer(obj: Factory): ProxyContainer? {
    val interceptor = obj.getCallback(0) as? ProxyInterceptor ?: return null
    return interceptor.container
  }

  fun getMutableContainer(obj: Factory): MutableProxyContainer? {
    val interceptor = obj.getCallback(0) as? ProxyInterceptor ?: return null
    return interceptor.container as? MutableProxyContainer
  }

  fun addProxy(obj: Any, proxy: ProxyMethod): Boolean {
    val container = getContainer(obj as Factory) as? MutableProxyContainer ?: return false
    container.addProxy(proxy)
    return true
  }

  inline operator fun <T : Any> get(
    clazz: Class<T>,
    container: MutableProxyContainer = defaultContainer(),
    builder: (Class<T>) -> T,
  ): Pair<T, MutableProxyContainer> {
    val target = getCachedTarget(clazz)

    val directAccessor = builder(target)
    val obj = builder(target)

    container.target = obj
    container.ctx[directAccessorKey] = directAccessor

    injectCallback(obj as Factory, container, directAccessor as Factory)

    return obj to container
  }

  inline fun <reified T : Any> get() = get(T::class.java)
  inline operator fun <reified T : Any> get(
    argumentTypes: Array<out Class<*>>,
    arguments: Array<out Any?>,
    container: MutableProxyContainer = defaultContainer(),
  ) = get(T::class.java, argumentTypes, arguments, container)

  inline operator fun <reified T : Any, reified A1 : Any?> get(
    a1: A1,
    container: MutableProxyContainer = defaultContainer(),
  ) = get<T>(arrayOf(A1::class.java), arrayOf(a1), container)

  inline operator fun <
    reified T : Any,
    reified A1 : Any?,
    reified A2 : Any?,
    > get(
    a1: A1, a2: A2,
    container: MutableProxyContainer = defaultContainer(),
  ) = get<T>(
    arrayOf(A1::class.java, A2::class.java),
    arrayOf(a1, a2),
    container,
  )

  inline operator fun <
    reified T : Any,
    reified A1 : Any?,
    reified A2 : Any?,
    reified A3 : Any?,
    > get(
    a1: A1, a2: A2, a3: A3,
    container: MutableProxyContainer = defaultContainer(),
  ) = get<T>(
    arrayOf(A1::class.java, A2::class.java, A3::class.java),
    arrayOf(a1, a2, a3),
    container,
  )

  inline operator fun <
    reified T : Any,
    reified A1 : Any?,
    reified A2 : Any?,
    reified A3 : Any?,
    reified A4 : Any?,
    > get(
    a1: A1, a2: A2, a3: A3, a4: A4,
    container: MutableProxyContainer = defaultContainer(),
  ) = get<T>(
    arrayOf(A1::class.java, A2::class.java, A3::class.java, A4::class.java),
    arrayOf(a1, a2, a3, a4),
    container,
  )

  inline operator fun <
    reified T : Any,
    reified A1 : Any?,
    reified A2 : Any?,
    reified A3 : Any?,
    reified A4 : Any?,
    reified A5 : Any?,
    > get(
    a1: A1, a2: A2, a3: A3, a4: A4, a5: A5,
    container: MutableProxyContainer = defaultContainer(),
  ) = get<T>(
    arrayOf(A1::class.java, A2::class.java, A3::class.java, A4::class.java, A5::class.java),
    arrayOf(a1, a2, a3, a4, a5),
    container,
  )

  operator fun <T : Any> get(clazz: Class<T>, container: MutableProxyContainer = defaultContainer()) =
    get(clazz, container, Class<T>::newInstance)

  operator fun <T : Any> get(
    clazz: Class<T>,
    argumentTypes: Array<out Class<*>>,
    arguments: Array<out Any?>,
    container: MutableProxyContainer = defaultContainer(),
  ) = get(clazz, container) {
    it.getConstructor(*argumentTypes).newInstance(*arguments)
  }

  fun <T : Any> newEnhancer(clazz: Class<T>, vararg interfaces: Class<*>): Enhancer {
    val enhancer = Enhancer()

    enhancer.setSuperclass(clazz)
    if (interfaces.isNotEmpty()) {
      enhancer.setInterfaces(interfaces)
    }

    val methods = clazz.allMethodsSequence.filter { !it.isPrivate() }.toList()

    enhancer.setCallbackTypes(Array(methods.size) { MethodInterceptor::class.java })
    enhancer.setCallbackFilter(methods::indexOf)

    return enhancer
  }

  @JvmOverloads
  fun injectCallback(
    obj: Factory,
    container: ProxyContainer = defaultContainer(),
    target: Factory = obj,
  ): ProxyContainer {
    if (obj.getCallback(0) != null && obj.getCallback(0) is ProxyInterceptor) {
      return (obj.getCallback(0) as ProxyInterceptor).container
    }

    repeat(obj.callbacks.size) {
      obj.setCallback(it, LocalCachedProxyInterceptor(container, target))
    }
    return container
  }

  fun <T : Any> getCachedTarget(clazz: Class<T>): Class<T> = cache.computeIfAbsent(clazz) {
    newEnhancer(clazz).createClass()
  }.uncheckedCast()

  fun <T : Any> getSuperCaller(
    obj: T,
  ): T = getContainer(obj as Factory)?.ctx?.get(directAccessorKey).uncheckedCast()

  fun addNonProxyClass(target: Factory, nonProxyClass: Class<*>): Boolean {
    val container = getMutableContainer(target) ?: throw IllegalArgumentException()
    return container.nonProxyClasses.add(nonProxyClass)
  }

  fun removeNonProxyClass(target: Factory, nonProxyClass: Class<*>): Boolean {
    val container = getMutableContainer(target) ?: throw IllegalArgumentException()
    return container.nonProxyClasses.remove(nonProxyClass)
  }

  fun clearCallbackCache(target: Factory, container: ProxyContainer) {
    target.callbacks.forEachIndexed { index, callback ->
      if (callback == null) {
        target.setCallback(index, LocalCachedProxyInterceptor(container, target))
        return@forEachIndexed
      }

      if (callback !is CachedMethodInterceptor) {
        return@forEachIndexed
      }

      callback.clearCache()
    }
  }
}
