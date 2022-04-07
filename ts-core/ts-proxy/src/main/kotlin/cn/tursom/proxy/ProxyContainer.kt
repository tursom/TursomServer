package cn.tursom.proxy

import cn.tursom.core.uncheckedCast

data class ProxyResult<out R>(
  val result: R,
  val success: Boolean = false,
) {
  companion object {
    val failed: ProxyResult<*> = ProxyResult<Any?>(null, false)
    fun <R> of(): ProxyResult<R?> {
      return of(null)
    }

    /**
     * 返回一个临时使用的 Result 对象
     * 因为是临时对象，所以不要把这个对象放到任何当前函数堆栈以外的地方
     * 如果要长期储存对象请 new Result
     */
    fun <R> of(result: R): ProxyResult<R> {
      return ProxyResult(result, true)
    }

    fun <R> failed(): ProxyResult<R> {
      return failed.uncheckedCast()
    }
  }
}

interface ProxyContainer : Iterable<ProxyMethod> {
}

inline fun ProxyContainer.forEachProxy(action: (ProxyMethod) -> Unit) {
  for (t in this) {
    action(t)
  }
}

inline fun <R> ProxyContainer.forFirstProxy(action: (ProxyMethod) -> ProxyResult<R>?): ProxyResult<R> {
  for (t in this) {
    val result = action(t)
    if (result != null && result.success) {
      return result
    }
  }
  return ProxyResult.failed()
}