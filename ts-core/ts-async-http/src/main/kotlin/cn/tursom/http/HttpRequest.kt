package cn.tursom.http

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

@Suppress("unused", "MemberVisibilityCanBePrivate")
object HttpRequest {
  val defaultHeader = mapOf(
    "accept" to "*/*",
    "connection" to "Keep-Alive",
    "user-agent" to "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)",
    "Accept-Encoding" to "gzip, deflate, sdch, br"
  )

  fun URLConnection.getCharset(): Charset {
    val contentType = getHeaderField("content-type")
    return if (contentType == null) {
      Charsets.UTF_8
    } else {
      val startIndex = contentType.indexOf("charset=", ignoreCase = true) + 8
      if (startIndex < 8) {
        Charsets.UTF_8
      } else {
        var endIndex = contentType.indexOf(";", startIndex = startIndex, ignoreCase = true)
        if (endIndex < 0) endIndex = contentType.length
        if (startIndex == endIndex) {
          Charsets.UTF_8
        } else {
          Charset.forName(contentType.substring(startIndex, endIndex))
        }
      }
    }
  }

  fun URLConnection.getRealInputStream(): InputStream {
    return if (getHeaderField("content-encoding")?.contains("gzip", true) == true) {
      GZIPInputStream(inputStream)
    } else {
      inputStream
    }
  }

  fun send(
    method: String = "GET",
    url: String,
    headers: Map<String, String> = defaultHeader,
    data: ByteBuffer? = null
  ): HttpURLConnection {
    val realUrl = URL(url)
    val conn = realUrl.openConnection() as HttpURLConnection
    headers.forEach { (key, value) ->
      conn.setRequestProperty(key, value)
    }
    if (data != null) conn.doOutput = true
    conn.doInput = true
    conn.requestMethod = method

    data?.let {
      conn.outputStream.use { out ->
        data.writeTo(out)
        out.flush()
      }
    }

    return conn
  }

  fun send(
    method: String = "GET",
    url: String,
    headers: Map<String, String> = defaultHeader,
    data: ByteArray?
  ) = send(method, url, headers, data?.let { HeapByteBuffer(data) })

  fun getContextStream(
    method: String = "GET",
    url: String,
    headers: Map<String, String> = defaultHeader,
    data: ByteBuffer? = null
  ): InputStream = send(method, url, headers, data).inputStream

  fun getContext(
    method: String = "GET",
    url: String,
    headers: Map<String, String> = defaultHeader,
    data: ByteBuffer? = null
  ) = send(method, url, headers, data).getRealInputStream().readBytes()

  fun getContextStr(
    method: String = "GET",
    url: String,
    headers: Map<String, String> = defaultHeader,
    data: ByteBuffer? = null
  ): String {
    val conn = send(method, url, headers, data)
    return conn.getRealInputStream().readBytes().toString(conn.getCharset())
  }

  fun doGet(
    url: String,
    param: String? = null,
    headers: Map<String, String> = defaultHeader
  ): String = getContextStr(
    "GET", if (param != null) {
      "$url?$param"
    } else {
      url
    }, headers
  )

  infix operator fun get(url: String): String = doGet(url, null)

  fun doGet(
    url: String,
    param: Map<String, String>,
    headers: Map<String, String> = defaultHeader
  ): String {
    val paramSB = StringBuilder()
    return doGet(url, run {
      param.forEach {
        paramSB.append("${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}&")
      }
      if (paramSB.isNotEmpty()) paramSB.deleteCharAt(paramSB.lastIndex)
      paramSB.toString()
    }, headers)
  }

  fun doPost(
    url: String,
    data: ByteArray,
    headers: Map<String, String> = defaultHeader
  ): String = getContextStr("POST", url, headers, HeapByteBuffer(data))

  fun doPost(
    url: String,
    param: Map<String, String>,
    headers: Map<String, String> = defaultHeader
  ): String {
    val sb = StringBuilder()
    param.forEach { (key, value) ->
      sb.append("${URLEncoder.encode(key, "utf-8")}=${URLEncoder.encode(value, "utf-8")}&")
    }
    if (sb.isNotEmpty()) sb.deleteCharAt(sb.lastIndex)
    return doPost(url, sb.toString().toByteArray(), headers)
  }

  fun doHead(
    url: String,
    param: String,
    headers: Map<String, String> = defaultHeader
  ): Map<String, List<String>> = send("HEAD", "$url?$param", headers).headerFields

  fun doHead(
    url: String,
    param: Map<String, String>,
    headers: Map<String, String> = defaultHeader
  ): Map<String, List<String>> {
    val paramSB = StringBuilder()
    return doHead(url, run {
      param.forEach {
        paramSB.append("${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}&")
      }
      if (paramSB.isNotEmpty()) paramSB.deleteCharAt(paramSB.lastIndex)
      paramSB.toString()
    }, headers)
  }

}