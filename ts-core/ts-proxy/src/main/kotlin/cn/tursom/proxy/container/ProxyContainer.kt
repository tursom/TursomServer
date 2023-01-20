package cn.tursom.proxy.container

import cn.tursom.core.context.ArrayContextEnv
import cn.tursom.core.context.Context

interface ProxyContainer : Iterable<Any> {
  companion object {
    val ctxEnv = ArrayContextEnv()
  }

  // to impl, use override val context: Context = ProxyContainer.contextEnv.newContext()
  val ctx: Context
  val target: Any
  val nonProxyClasses: Set<Class<*>>
}