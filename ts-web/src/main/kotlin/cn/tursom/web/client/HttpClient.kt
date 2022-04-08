package cn.tursom.web.client

interface HttpClient {
  suspend fun request(method: String, url: String, ssl: Boolean? = null): HttpRequest
}
