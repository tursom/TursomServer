package cn.tursom.web.netty

import cn.tursom.web.ResponseHeaderAdapter
import io.netty.handler.codec.http.HttpHeaders
import java.util.HashMap

@Suppress("MemberVisibilityCanBePrivate")
open class NettyResponseHeaderAdapter : ResponseHeaderAdapter {
  val responseMap = HashMap<String, Any>()
  val responseListMap = HashMap<String, ArrayList<Any>>()

  override fun setResponseHeader(name: String, value: Any) {
    responseMap[name] = value
    responseListMap.remove(name)
  }

  override fun addResponseHeader(name: String, value: Any) {
    val list = responseListMap[name] ?: run {
      val newList = ArrayList<Any>()
      responseListMap[name] = newList
      newList
    }
    responseMap[name]?.let {
      responseMap.remove(name)
      list.add(it)
    }
    list.add(value)
  }

  protected fun addHeaders(heads: HttpHeaders, defaultHeaders: Map<out CharSequence, Any>) {
    responseListMap.forEach { (t, u) ->
      u.forEach {
        heads.add(t, it)
      }
    }

    responseMap.forEach { (t, u) ->
      heads.set(t, u)
    }

    defaultHeaders.forEach { (t, u) ->
      if (!heads.contains(t)) {
        heads.set(t, u)
      }
    }
  }
}