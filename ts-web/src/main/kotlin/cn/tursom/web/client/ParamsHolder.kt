package cn.tursom.web.client

import cn.tursom.core.uncheckedCast
import cn.tursom.core.urlEncode

interface ParamsHolder<out T> : ParamsSetter<T> {
  val params: MutableMap<String, MutableList<String>>
  val paramStr: String
    get() = buildString {
      params.forEach { (k, list) ->
        list.forEach { v ->
          if (isNotEmpty()) {
            append('&')
          }
          append("$k=$v")
        }
      }
    }

  override fun addParam(key: String, value: String): T {
    params.getOrPut(key.urlEncode) { ArrayList() }.add(value.urlEncode)
    return uncheckedCast()
  }
}