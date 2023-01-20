package cn.tursom.proxy.interceptor

import cn.tursom.reflect.asm.ReflectAsmInvoker
import cn.tursom.reflect.asm.ReflectAsmUtils
import com.esotericsoftware.reflectasm.MethodAccess
import net.sf.cglib.proxy.InvocationHandler
import java.lang.reflect.Method

class LocalCachedNoProxyInvocationHandler(
  val proxy: Any,
) : InvocationHandler {
  private var handler: (method: Method?, args: Array<out Any>?) -> Any? = DefaultHandler()

  override fun invoke(ignore: Any, method: Method?, args: Array<out Any>?): Any? {
    return handler(method, args)
  }

  private inner class DefaultHandler : (Method?, Array<out Any>?) -> Any? {
    override fun invoke(method: Method?, args: Array<out Any>?): Any? {
      method!!

      val (methodAccess, index) = ReflectAsmUtils.getMethodByRegex(
        proxy.javaClass,
        "CGLIB\\\$${method.name}\\\$.*".toRegex(),
        *method.parameterTypes,
        method.returnType,
      )!!
      handler = MethodAccessHandler(methodAccess, index)
      return ReflectAsmInvoker.invoke(methodAccess, proxy, index, args)
    }
  }

  private inner class MethodAccessHandler(
    private val methodAccess: MethodAccess,
    private val index: Int,
  ) : (Method?, Array<out Any>?) -> Any? {
    override fun invoke(method: Method?, args: Array<out Any>?) =
      ReflectAsmInvoker.invoke(methodAccess, proxy, index, args)
  }
}