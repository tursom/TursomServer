package cn.tursom.proxy

import cn.tursom.core.uncheckedCast

data class ProxyResult<out R>(
  val result: R,
  val success: Boolean = false,
) {
  companion object {
    val success: ProxyResult<*> = ProxyResult<Any?>(null, true)
    val failed: ProxyResult<*> = ProxyResult<Any?>(null, false)

    fun <R> of(): ProxyResult<R?> = success.uncheckedCast()
    fun <R> of(result: R): ProxyResult<R> = if (result == null) {
      success.uncheckedCast()
    } else {
      ProxyResult(result, true)
    }

    fun <R> failed(): ProxyResult<R> = failed.uncheckedCast()
  }
}
