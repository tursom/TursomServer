package cn.tursom.proxy.function

import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import cn.tursom.reflect.asm.ReflectAsmUtils
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

object CallSuperProxyMethodCacheFunction : ProxyMethodCacheFunction {
  operator fun get(obj: Any, method: Method): ProxyMethodCacheFunction {
    val (methodAccess, index) = ReflectAsmUtils.getMethodByRegex(
      obj.javaClass,
      "CGLIB\\\$${method.name}\\\$.*".toRegex(),
      *method.parameterTypes,
      method.returnType,
    ) ?: return CallSuperProxyMethodCacheFunction

    return ReflectASMProxyMethodInvoker(obj, methodAccess, index).toJava()
  }

  override fun invoke(
    obj: Any?,
    c: ProxyContainer,
    method: Method?,
    args: Array<out Any?>?,
    proxy: MethodProxy?,
  ): Any? {
    return proxy!!.invokeSuper(obj, args)
  }
}