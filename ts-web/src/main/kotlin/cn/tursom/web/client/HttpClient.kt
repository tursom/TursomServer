package cn.tursom.web.client

interface HttpClient<T : HttpRequest<T>> {
  suspend fun request(method: String, url: String): T
}
