package cn.tursom.web.client

import cn.tursom.core.util.uncheckedCast

interface ParamsSetter<out T> {
  fun addParam(key: String, value: String): T
  fun addParams(params: Map<String, String>): T {
    params.forEach(::addParam)
    return uncheckedCast()
  }
}
