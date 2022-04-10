package cn.tursom.web.client.netty

import io.netty.handler.codec.compression.Brotli
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class NettyHttpClientTest {
  private val client = NettyHttpClient()

  @Test
  fun request() {
    Brotli.ensureAvailability()
    runBlocking {
      val request = client.request("GET", "https://cdn.segmentfault.com/r-e032f7ee/umi.js")
        .addHeader("accept-encoding", "br")
      val response = request.send()
      println(response.body.string())
    }
  }
}