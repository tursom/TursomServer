package cn.tursom.proxy.function

import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import cn.tursom.reflect.asm.ReflectAsmUtils
import com.esotericsoftware.reflectasm.MethodAccess
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

internal class ReflectASMProxyMethodInvoker(
  private val self: Any,
  private val methodAccess: MethodAccess,
  private val index: Int,
) : ProxyMethodCacheFunction {
  companion object {
    private val fastInvoker: MethodAccess.(Any, Int, Array<out Any?>?) -> Any? = MethodAccess::invoke

    operator fun get(
      proxy: Any,
      method: Method,
    ): ReflectASMProxyMethodInvoker? {
      val reflectAsmMethod = try {
        ReflectAsmUtils.getMethod(
          proxy.javaClass,
          method.name,
          paramTypes = method.parameterTypes,
          returnType = method.returnType,
        )
      } catch (e: Exception) {
        e.printStackTrace()
        null
      } ?: return null

      val (methodAccess, index) = reflectAsmMethod
      return ReflectASMProxyMethodInvoker(proxy, methodAccess, index)
    }
  }

  override fun invoke(
    obj: Any?,
    c: ProxyContainer,
    method: Method?,
    args: Array<out Any?>?,
    proxy: MethodProxy?,
  ): Any? {
    return fastInvoker(methodAccess, self, index, args)
  }
}
