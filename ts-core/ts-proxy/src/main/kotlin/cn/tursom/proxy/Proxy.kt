package cn.tursom.proxy

import cn.tursom.core.uncheckedCast
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.Factory
import java.util.concurrent.ConcurrentHashMap

object Proxy {
  object CallSuperException : Exception()

  val callSuper = object : ThreadLocal<Boolean>() {
    override fun initialValue(): Boolean = throw CallSuperException
    override fun get(): Boolean = try {
      super.get()
    } catch (_: CallSuperException) {
      false
    }
  }

  var defaultContainer: () -> MutableProxyContainer = { ListProxyContainer() }
  private val cache = ConcurrentHashMap<Class<*>, Class<*>>()

  fun getContainer(obj: Any): ProxyContainer? {
    if (obj !is Factory) return null
    val interceptor = obj.getCallback(0) as? ProxyInterceptor ?: return null
    return interceptor.container
  }

  fun addProxy(obj: Any, proxy: ProxyMethod): Boolean {
    val container = getContainer(obj) as? MutableProxyContainer ?: return false
    container.addProxy(proxy)
    return true
  }

  inline operator fun <T : Any> get(
    clazz: Class<T>,
    container: MutableProxyContainer = defaultContainer(),
    builder: (Class<T>) -> T,
  ): Pair<T, MutableProxyContainer> {
    val target = getCachedTarget(clazz)
    val obj = builder(target)
    injectCallback(obj as Factory, container)
    return obj to container
  }

  inline fun <reified T : Any> get() = get(T::class.java)
  inline fun <reified T : Any> get(
    argumentTypes: Array<out Class<*>>,
    arguments: Array<out Any?>,
    container: MutableProxyContainer = defaultContainer(),
  ) = get(T::class.java, argumentTypes, arguments, container)

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
    enhancer.setCallbackType(ProxyInterceptor::class.java)
    enhancer.setCallbackFilter { 0 }
    return enhancer
  }

  @JvmOverloads
  fun injectCallback(obj: Any, container: ProxyContainer = defaultContainer()): ProxyContainer {
    obj as Factory
    if (obj.getCallback(0) != null && obj.getCallback(0) != ProxyDispatcher) {
      return (obj.getCallback(0) as ProxyInterceptor).container
    }

    obj.setCallback(0, ProxyInterceptor(container))
    return container
  }

  fun <T : Any> getCachedTarget(clazz: Class<T>): Class<T> = cache.computeIfAbsent(clazz) {
    newEnhancer(clazz).createClass()
  }.uncheckedCast()
}
