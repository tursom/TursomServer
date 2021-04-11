package cn.tursom.web.netty

import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.QueryStringDecoder
import io.netty.handler.codec.http.multipart.Attribute
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import kotlin.collections.set

/**
 * HTTP请求参数解析器, 支持GET, POST
 */
object ParamParser {
  fun parse(req: HttpRequest): MutableMap<String, List<String>> {
    val paramMap = HashMap<String, List<String>>()
    when (req.method()) {
      HttpMethod.GET -> try {
        // 是GET请求
        val decoder = QueryStringDecoder(req.uri())
        decoder.parameters().entries.forEach { entry ->
          paramMap[entry.key] = entry.value
        }
      } catch (e: Exception) {
      }
      HttpMethod.POST -> if (req is HttpContent) {
        // 是POST请求
        parse(req, req, paramMap)
      }
    }
    return paramMap
  }

  fun parse(
    req: HttpRequest,
    body: HttpContent,
    paramMap: MutableMap<String, List<String>>
  ): MutableMap<String, List<String>> {
    try {
      val decoder = HttpPostRequestDecoder(req)
      decoder.offer(body)

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
    return paramMap
  }
}