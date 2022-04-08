package cn.tursom.proxy

import cn.tursom.core.context.ArrayContextEnv
import cn.tursom.core.context.Context

interface ProxyContainer : Iterable<ProxyMethod> {
  companion object {
    val contextEnv = ArrayContextEnv()

    inline fun ProxyContainer.forEachProxy(action: (ProxyMethod) -> Unit) {
      for (t in this) {
        action(t)
      }
    }

    inline fun <R> ProxyContainer.forFirstProxy(action: (ProxyMethod) -> ProxyResult<R>): ProxyResult<R> {
      for (t in this) {
        val result = action(t)
        if (result.success) {
          return result
        }
      }
      return ProxyResult.failed()
    }
  }

  // to impl, use override val context: Context = ProxyContainer.contextEnv.newContext()
  val context: Context
  val lastModify: Long
}