package cn.tursom.aop.advice

import cn.tursom.aop.ProxyHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

@Suppress("MemberVisibilityCanBePrivate")
class AdviceContent(
  val target: Any,
  val method: Method,
  val args: Array<out Any>?
) {
  val bean: Any = if (Proxy.isProxyClass(target.javaClass)) {
    val handler = Proxy.getInvocationHandler(target)
    if (handler is ProxyHandler)
      handler.getTopBean()
    else target
  } else target

  fun invoke() {
    if (args != null) {
      method.invoke(target, *args)
    } else {
      method.invoke(target)
    }
  }
}