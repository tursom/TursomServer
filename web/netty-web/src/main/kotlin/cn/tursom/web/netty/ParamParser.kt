package cn.tursom.web.netty

import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.QueryStringDecoder
import io.netty.handler.codec.http.multipart.Attribute
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

/**
 * HTTP请求参数解析器, 支持GET, POST
 */
object ParamParser {
  fun parse(fullReq: FullHttpRequest): HashMap<String, List<String>> {
    val method = fullReq.method()

    val paramMap = HashMap<String, List<String>>()

    when (method) {
      HttpMethod.GET -> try {
        // 是GET请求
        val decoder = QueryStringDecoder(fullReq.uri())
        decoder.parameters().entries.forEach { entry ->
          paramMap[entry.key] = entry.value
        }
      } catch (e: Exception) {
      }
      HttpMethod.POST -> try {
        // 是POST请求
        val decoder = HttpPostRequestDecoder(fullReq)
        decoder.offer(fullReq)

        val paramList = decoder.bodyHttpDatas

        for (param in paramList) {
          val data = param as Attribute
          if (!paramMap.containsKey(data.name)) {
            paramMap[data.name] = ArrayList()
          }
          (paramMap[data.name] as ArrayList).add(data.value)
        }
      } catch (e: Exception) {
      }
    }

    return paramMap
  }
}