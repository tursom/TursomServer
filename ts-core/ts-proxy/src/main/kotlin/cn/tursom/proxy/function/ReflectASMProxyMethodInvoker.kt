package cn.tursom.proxy.function

import cn.tursom.proxy.container.ProxyContainer
import cn.tursom.proxy.container.ProxyMethodCacheFunction
import cn.tursom.reflect.asm.ReflectAsmInvoker
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
    operator fun get(
      proxy: Any,
      method: Method,
    ): ReflectASMProxyMethodInvoker? {
      val reflectAsmMethod = try {
        val methodParamTypes = method.parameterTypes
        val matchedMethods = ReflectAsmUtils.getMethodSequence(
          proxy.javaClass,
          method.name,
          paramTypes = methodParamTypes,
          returnType = method.returnType,
        ).toList()
        when {
          matchedMethods.isEmpty() -> null

          matchedMethods.size == 1 -> matchedMethods.first()

          else -> matchedMethods.minBy { (methodAccess, methodIndex) ->
            var parameterIndex = 0
            methodAccess.parameterTypes[methodIndex].sumOf { parameterType ->
              if (methodParamTypes[parameterIndex++] != parameterType) {
                0L
              } else {
                1L
              }
            }
          }

        }
      } catch (e: Exception) {
        e.printStackTrace()
        null
      } ?: return null

      val (methodAccess, index) = reflectAsmMethod
      return ReflectASMProxyMethodInvoker(proxy, methodAccess, index)
    }
  }

  fun toJava() = JavaReflectASMProxyMethodInvoker(self, methodAccess, index)

  override fun invoke(
    obj: Any?,
    c: ProxyContainer,
    method: Method?,
    args: Array<out Any?>?,
    proxy: MethodProxy?,
  ): Any? {
    return ReflectAsmInvoker.invoke(methodAccess, self, index, args)
  }
}
