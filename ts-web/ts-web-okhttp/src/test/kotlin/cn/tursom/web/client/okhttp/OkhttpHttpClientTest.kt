package cn.tursom.web.client.okhttp

import cn.tursom.log.impl.Slf4jImpl
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test
import java.util.concurrent.TimeUnit


private fun okhttpclient(): OkHttpClient {
  val logInterceptor = HttpLoggingInterceptor(HttpLogger())
  logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
  return OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .addNetworkInterceptor(logInterceptor)
    .build()
}

internal class OkhttpHttpClientTest {
  private val client = OkhttpHttpClient(okhttpclient())

  @Test
  fun request() {
    runBlocking {
      val response = client.request(
        "GET",
        "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_history",
      ).addParams(mapOf(
        "type" to "8",
        "from" to "",
        "platform" to "web",
        "uid" to "1837471",
        "offset_dynamic_id" to "645924525475627026",
      )).addHeaders(mapOf(
        "Cookie" to "l=v; buvid3=13A9BD44-479F-4FC1-AA29-8EB2C3206B6674612infoc; i-wanna-go-back=-1; _uuid=9B77BDF4-773C-2101D-5F10F-E4665164DFE674981infoc; buvid4=8DD0C63B-7F1C-D4DC-AC8D-4CE99C5200C075805-022040411-FRmBv7s/ltkIAOlM97A+aQ%3D%3D; sid=it9v6m7q; buvid_fp_plain=undefined; DedeUserID=1837471; DedeUserID__ckMd5=5678a12a5b7d1691; SESSDATA=48f843c6%2C1664594400%2C9e2b9*41; bili_jct=02bad9065c947f90286a1bcd937d7ea8; b_ut=5; buvid_fp=2f9e2646e762e56e33e0c37ee7338300; LIVE_BUVID=AUTO3116490424152121; CURRENT_BLACKGAP=0; blackside_state=0; rpdid=",
        "Origin" to "https://t.bilibili.com",
        "Referer" to "https://t.bilibili.com/",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36",
      )).send()
      //val response = client.request("GET", "https://cdn.segmentfault.com/r-e032f7ee/umi.js")
      //  .addHeader("accept-encoding", "gzip, deflate, br")
      //  .send()
      response.response.protocol
      println(response.body.string())
    }
  }
}

class HttpLogger : HttpLoggingInterceptor.Logger {
  companion object : Slf4jImpl()

  override fun log(message: String) {
    logger.info("HttpLogInfo: {}", message)
  }

}