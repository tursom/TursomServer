package cn.tursom.web

import cn.tursom.web.utils.parseRange

interface RequestHeaderAdapter {
  val cookieMap: Map<String, String>
  val requestHost: String? get() = getHeader("Host")
  fun getHeader(header: String): String?
  fun getHeaders(): List<Map.Entry<String, String>>

  fun getCookie(name: String): String? = cookieMap[name]

  fun getCacheTag(): String? = getHeader("If-None-Match")
  fun getRequestRange(): List<Pair<Int, Int>>? {
    val range = getHeader("Range") ?: return null
    return parseRange(range)
  }
}