package cn.tursom.proxy.container

import cn.tursom.proxy.container.ProxyContainer
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

fun interface ProxyMethodCacheFunction : (
  Any?,
  ProxyContainer,
  Method?,
  Array<out Any?>?,
  MethodProxy?,
) -> Any?
