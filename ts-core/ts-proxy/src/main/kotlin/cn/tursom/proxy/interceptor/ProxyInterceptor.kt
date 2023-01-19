package cn.tursom.proxy.interceptor

import cn.tursom.proxy.annotation.ForEachProxy
import cn.tursom.proxy.annotation.ForFirstProxy
import cn.tursom.proxy.container.ListProxyContainer
import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCache
import cn.tursom.proxy.function.CallSuperProxyMethodCacheFunction
import cn.tursom.proxy.function.OnForEachProxyImpl
import cn.tursom.proxy.function.OnForFirstProxyImpl
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

open class ProxyInterceptor(
  val container: ProxyContainer = ListProxyContainer(),
  val nonProxyClasses: MutableSet<Class<*>> = HashSet(listOf(Any::class.java)),
) : MethodInterceptor {
  override fun intercept(obj: Any?, method: Method?, args: Array<out Any?>?, proxy: MethodProxy): Any? {
    val cache = container.ctx[ProxyMethodCache.ctxKey]
    var handler = cache[proxy]
    if (handler != null) {
      return handler(obj, container, method, args, proxy)
    }

    method!!

    nonProxyClasses.forEach { nonProxyClass ->
      nonProxyClass.declaredMethods.forEach {
        if (it.name == method.name &&
          it.returnType.isAssignableFrom(method.returnType) &&
          it.parameterTypes.contentEquals(method.parameterTypes)
        ) {
          cache.update(proxy, CallSuperProxyMethodCacheFunction)
          return proxy.invokeSuper(obj, args)
        }
      }
    }

    //var handler: ProxyMethodCacheFunction? = null

    for (annotation in method.annotations) when (annotation) {
      is ForEachProxy -> {
        handler = OnForEachProxyImpl(annotation)
        break
      }

      is ForFirstProxy -> {
        handler = OnForFirstProxyImpl(annotation)
        break
      }
    }
    if (handler == null) {
      handler = OnForFirstProxyImpl(ForFirstProxy.EMPTY)
    }

    return handler(obj, container, method, args, proxy)
  }
}
