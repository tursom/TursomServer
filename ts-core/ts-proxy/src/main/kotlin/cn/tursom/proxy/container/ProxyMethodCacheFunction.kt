package cn.tursom.proxy.container

import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

fun interface ProxyMethodCacheFunction {
  operator fun invoke(
    obj: Any?,
    c: ProxyContainer,
    method: Method?,
    args: Array<out Any?>?,
    proxy: MethodProxy?,
  ): Any?
}
