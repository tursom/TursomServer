package cn.tursom.web.client.okhttp

import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class OkhttpHttpClientTest {
  private val client = OkhttpHttpClient.default

  @Test
  fun request() {
    runBlocking {
      val response = client.request("GET", "https://cdn.segmentfault.com/r-e032f7ee/umi.js")
        .addHeader("accept-encoding", "gzip, deflate, br")
        .send()
      println(response.body.string())
    }
  }
}