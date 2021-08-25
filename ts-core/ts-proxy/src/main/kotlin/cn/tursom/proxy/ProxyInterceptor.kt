package cn.tursom.proxy

import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

class ProxyInterceptor : MethodInterceptor {
  companion object {
    private val HANDLE_DEQUE_THREAD_LOCAL = ThreadLocal<ArrayDeque<Any>>()
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

    private fun isOnProxyMethod(method: Method): Boolean {
      return equalsMethod(method, "onProxy", parameterTypes)
    }

    private val handleDeque: ArrayDeque<Any>
      get() {
        var objectArrayDeque = HANDLE_DEQUE_THREAD_LOCAL.get()
        if (objectArrayDeque == null) {
          objectArrayDeque = ArrayDeque()
          HANDLE_DEQUE_THREAD_LOCAL.set(objectArrayDeque)
        }
        return objectArrayDeque
      }

    @Suppress("UNCHECKED_CAST")
    fun <T> getHandle(): T {
      return handleDeque.first as T
    }

    private fun push(obj: Any) {
      handleDeque.push(obj)
    }

    private fun pop() {
      handleDeque.pop()
    }
  }

  @Throws(Throwable::class)
  override fun intercept(obj: Any, method: Method, args: Array<out Any?>, proxy: MethodProxy): Any? {
    push(obj)
    return try {
      if (obj is ProxyContainer<*> && !isOnProxyMethod(method)) {
        val result = obj.onProxy(method, args, proxy)
        if (result != null && result.success) {
          return result.result
        }
      }
      proxy.invokeSuper(obj, args)
    } finally {
      pop()
    }
  }
}