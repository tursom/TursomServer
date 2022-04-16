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

  @Test
  fun testH2() {
    runBlocking {
      testH2suspend()
    }
  }

  suspend fun testH2suspend() {
    val client = NettyHttpClient()
    //client.get("https://www.baidu.com/").send()
    //return
    val request = client.request(
      "GET",
      "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/dynamic_history",
    ).addParams(mapOf(
      "type" to "8",
      "from" to "",
      "platform" to "web",
      "uid" to "1837471",
      "offset_dynamic_id" to "645924525475627026",
    )).addHeaders(mapOf(
      //"Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
      //"Accept-Encoding" to "gzip, deflate, br",
      //"Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,ja;q=0.6",
      //"Cache-Control" to "max-age=0",
      //"Sec-Ch-Ua" to "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"100\", \"Google Chrome\";v=\"100\"",
      //"Sec-Ch-Ua-Mobile" to "?0",
      //"Sec-Ch-Ua-Platform" to "\"Windows\"",
      //"Sec-Fetch-Dest" to "document",
      //"Sec-Fetch-Mode" to "navigate",
      //"Sec-Fetch-Site" to "none",
      //"Sec-Fetch-User" to "?1",
      //"Upgrade-Insecure-Requests" to "1",
      "Cookie" to "l=v; buvid3=13A9BD44-479F-4FC1-AA29-8EB2C3206B6674612infoc; i-wanna-go-back=-1; _uuid=9B77BDF4-773C-2101D-5F10F-E4665164DFE674981infoc; buvid4=8DD0C63B-7F1C-D4DC-AC8D-4CE99C5200C075805-022040411-FRmBv7s/ltkIAOlM97A+aQ%3D%3D; sid=it9v6m7q; buvid_fp_plain=undefined; DedeUserID=1837471; DedeUserID__ckMd5=5678a12a5b7d1691; SESSDATA=48f843c6%2C1664594400%2C9e2b9*41; bili_jct=02bad9065c947f90286a1bcd937d7ea8; b_ut=5; buvid_fp=2f9e2646e762e56e33e0c37ee7338300; LIVE_BUVID=AUTO3116490424152121; blackside_state=0; CURRENT_BLACKGAP=0; rpdid=|(umRk|JmYJm0J'uYRm|Rk)JR; nostalgia_conf=-1; CURRENT_QUALITY=112; fingerprint=2f9e2646e762e56e33e0c37ee7338300; fingerprint3=4d4eeb83e1c12babb9274ebc0470c3bb; innersign=1; CURRENT_FNVAL=4048; bsource=share_source_qqchat; bp_video_offset_1837471=647420312255725600; PVID=6; bp_t_offset_1837471=647481829084889088; b_lsid=4B24DCFF_18013AB5E6E",
      "Origin" to "https://t.bilibili.com",
      "Referer" to "https://t.bilibili.com/",
      "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36",
    ))
    request.version = "h2"
    val response = request.send()
    println(response.body.string())
  }
}