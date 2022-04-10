package cn.tursom.web.client.netty

import cn.tursom.web.client.HttpClient
import cn.tursom.web.client.HttpRequest
import java.net.URI

open class NettyHttpClient : HttpClient {
  override suspend fun request(method: String, url: String): HttpRequest {
    val uri = URI.create(url)
    val port = if (uri.port < 0) {
      when (uri.scheme ?: "http") {
        "http" -> 80
        "https" -> 443
        else -> -1
      }
    } else {
      uri.port
    }
    val pool = HttpConnectionPool.poolOf(uri.host, port, uri.scheme == "https")
    val request = NettyHttpRequest(pool, uri.scheme, uri.host, uri.port, uri.path)
    request.method = method
    return request
  }
}
