package cn.tursom.proxy

import cn.tursom.core.uncheckedCast

interface Proxy<out T : ProxyMethod> : Iterable<T> {
  data class Result<out R>(
    val result: R,
    val success: Boolean = false,
  )

  companion object {
    val failed: Result<*> = Result<Any?>(null, false)
    fun <R> of(): Result<R?> {
      return of(null)
    }

    /**
     * 返回一个临时使用的 Result 对象
     * 因为是临时对象，所以不要把这个对象放到任何当前函数堆栈以外的地方
     * 如果要长期储存对象请 new Result
     */
    fun <R> of(result: R): Result<R> {
      return Result(result, true)
    }

    fun <R> failed(): Result<R> {
      return failed.uncheckedCast()
    }
  }
}

inline fun <T : ProxyMethod> Proxy<T>.forEachProxy(action: (T) -> Unit) {
  for (t in this) {
    action(t)
  }
}

inline fun <R, T : ProxyMethod> Proxy<T>.forFirstProxy(action: (T) -> Proxy.Result<R>?): Proxy.Result<R> {
  for (t in this) {
    val result = action(t)
    if (result != null && result.success) {
      return result
    }
  }
  return Proxy.failed()
}