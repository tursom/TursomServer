package cn.tursom.web

interface RequestHeaderAdapter {
  val requestHost: String? get() = getHeader("Host")
  fun getHeader(header: String): String?
  fun getHeaders(): List<Map.Entry<String, String>>

  fun getCacheTag(): String? = getHeader("If-None-Match")
}