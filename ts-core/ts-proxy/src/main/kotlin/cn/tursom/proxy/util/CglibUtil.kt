package cn.tursom.proxy.util

import cn.tursom.reflect.asm.ReflectAsmUtils
import net.sf.cglib.core.Signature
import net.sf.cglib.proxy.Callback
import net.sf.cglib.proxy.MethodProxy

object CglibUtil {
  fun getFactoryData(clazz: Class<*>): Any? {
    val (fieldAccess, i) = ReflectAsmUtils.getField(clazz, "CGLIB\$FACTORY_DATA")!!
    return fieldAccess.get(null, i)
  }

  fun getCallbackFilter(clazz: Class<*>): Any? {
    val field = clazz.getDeclaredField("CGLIB\$CALLBACK_FILTER")
    field.isAccessible = true
    return field.get(null)
  }

  fun findMethodProxy(clazz: Class<*>, signature: Signature): MethodProxy? {
    return ReflectAsmUtils.getStaticMethod1<Signature, MethodProxy?>(
      clazz,
      "CGLIB\$findMethodProxy"
    )!!(signature)
  }

  fun setThreadCallbacks(clazz: Class<*>, callbacks: Array<out Callback>) {
    ReflectAsmUtils.getStaticMethod1<Array<out Callback>, Unit?>(
      clazz,
      "CGLIB\$SET_THREAD_CALLBACKS"
    )!!(callbacks)
  }

  fun setStaticCallbacks(clazz: Class<*>, callbacks: Array<out Callback>) {
    ReflectAsmUtils.getStaticMethod1<Array<out Callback>, Unit?>(
      clazz,
      "CGLIB\$SET_STATIC_CALLBACKS"
    )!!(callbacks)
  }
}
