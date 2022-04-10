package cn.tursom.web.client.okhttp

import cn.tursom.web.client.HttpResponse
import okhttp3.Response

class OkhttpHttpResponse(
  val response: Response,
) : HttpResponse {
  override val code: Int
    get() = response.code
  override val reason: String
    get() = response.message
  override val headers: Iterable<Pair<String, String>>
    get() = response.headers

  override fun getHeader(key: String): String? {
    return response.headers[key]
  }

  override fun getHeaders(key: String): List<String> {
    return response.headers.filter { it.first == key }.map { it.second }
  }

  override val body: OkhttpResponseStream
    get() = OkhttpResponseStream(response.body)
}
