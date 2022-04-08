package cn.tursom.web.client

interface HttpResponse {
  val code: Int
  val reason: String
  val headers: Iterable<Map.Entry<String, String>>
  fun getHeader(key: String): String?
  fun getHeaders(key: String): List<String>
  val body: HttpResponseStream
}
