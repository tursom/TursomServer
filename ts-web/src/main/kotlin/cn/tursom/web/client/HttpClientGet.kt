package cn.tursom.web.client

suspend fun <R : HttpRequest<R>> HttpClient<R>.get(
  url: String,
  params: Map<String, String> = emptyMap(),
  headers: Map<String, String> = emptyMap(),
): R {
  return request("GET", url)
    .addParams(params)
    .addHeaders(headers)
}
