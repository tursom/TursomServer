package cn.tursom.proxy

import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

class ProxyInterceptor(
  private val container: ProxyContainer = ListProxyContainer(),
  private val supportCallSuper: Boolean = true,
) : MethodInterceptor {
  companion object {
    val callSuper = ThreadLocal<Boolean?>()
    private val parameterTypes = arrayOf(Method::class.java, Array<Any>::class.java, MethodProxy::class.java)
    private val parameterTypesField: Field = Method::class.java.getDeclaredField("parameterTypes").apply {
      isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    private fun getParameterTypes(method: Method): Array<Class<*>> {
      return parameterTypesField[method] as Array<Class<*>>
    }

    fun equalsMethod(method: Method, name: String?, parameterTypes: Array<Class<*>>?): Boolean {
      return method.name == name && parameterTypes.contentEquals(getParameterTypes(method))
    }

    fun isOnProxyMethod(method: Method): Boolean {
      //return callSuper.get() == true || equalsMethod(method, "onProxy", parameterTypes)
      return callSuper.get() == true
    }
  }

  override fun intercept(obj: Any, method: Method, args: Array<out Any?>, proxy: MethodProxy): Any? {
    if (supportCallSuper && callSuper.get() == true) {
      callSuper.remove()
      return proxy.invokeSuper(obj, args)
    }

    val result = ProxyRunner.onProxy(obj, container, method, args, proxy)
    return if (result.success) {
      result.result
    } else {
      proxy.invokeSuper(obj, args)
    }
  }
}