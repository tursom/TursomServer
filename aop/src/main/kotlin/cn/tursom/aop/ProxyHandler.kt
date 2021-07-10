package cn.tursom.aop

import cn.tursom.aop.advice.AdviceContent
import cn.tursom.aop.aspect.Aspect
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class ProxyHandler(private val target: Any, private val aspect: Aspect) : InvocationHandler {

  fun getTopBean(): Any {
    var bean = target
    while (Proxy.isProxyClass(bean.javaClass)) {
      val handler = Proxy.getInvocationHandler(bean)
      if (handler is ProxyHandler)
        bean = handler.target
      else break
    }
    return bean
  }

  override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
    return if (aspect.pointcut.matchMethod(method)) {
      aspect.advice.invoke(AdviceContent(target, method, args))
    } else {
      method.invoke(target, args)
    }
  }

  companion object {
    fun proxyEnhance(bean: Any, aspect: Aspect): Any {
      val clazz = bean.javaClass
      return Proxy.newProxyInstance(
        clazz.classLoader,
        clazz.interfaces,
        ProxyHandler(bean, aspect)
      )
    }

    fun getTopBean(target: Any): Any {
      var bean = target
      while (Proxy.isProxyClass(bean.javaClass)) {
        val handler = Proxy.getInvocationHandler(bean)
        if (handler is ProxyHandler)
          bean = handler.target
        else break
      }
      return bean
    }
  }
}