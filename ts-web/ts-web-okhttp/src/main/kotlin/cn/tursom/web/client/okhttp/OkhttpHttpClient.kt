package cn.tursom.web.client.okhttp

import cn.tursom.web.client.HttpClient
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.SocketAddress

class OkhttpHttpClient(
  private val client: OkHttpClient,
) : HttpClient<OkhttpHttpRequest> {
  companion object {
    val direct = OkhttpHttpClient(OkHttpClient().newBuilder()
      .retryOnConnectionFailure(true)
      .build())
    val socket = proxy()
    val httpProxy = proxy(port = 8080, type = Proxy.Type.HTTP)

    var default = direct

    @JvmOverloads
    fun proxy(
      host: String = "127.0.0.1",
      port: Int = 1080,
      type: Proxy.Type = Proxy.Type.SOCKS,
      builder: OkHttpClient.Builder = OkHttpClient().newBuilder(),
    ) = OkhttpHttpClient(builder
      .proxy(Proxy(type, InetSocketAddress(host, port) as SocketAddress))
      .retryOnConnectionFailure(true)
      .build())
  }

  override suspend fun request(method: String, url: String): OkhttpHttpRequest {
    return OkhttpHttpRequest(client, method, url)
  }
}

