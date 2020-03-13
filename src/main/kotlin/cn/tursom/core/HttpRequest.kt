package cn.tursom.core

import java.io.File
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

/**
 * 向指定URL发送GET方法的请求
 *
 * @param url
 * 发送请求的URL
 * @param param
 * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
 * @return URL 所代表远程资源的响应结果
 * @deprecated
 */
fun sendGet(
  url: String,
  param: String? = null,
  headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
): String {
  val realUrl = URL(if (param != null) {
    "$url?$param"
  } else {
    url
  })
  // 打开和URL之间的连接
  val connection = realUrl.openConnection()
  // 设置请求属性
  headers.forEach {
    connection.addRequestProperty(it.key, it.value)
  }
  // 建立实际的连接
  connection.connect()
  // 获取所有响应头字段
  //val map = connection.headerFields
  // 遍历所有的响应头字段
//			for (key in map.keys) {
//				println(key + "--->" + map[key])
//			}
  // 定义 BufferedReader输入流来读取URL的响应
  return connection.getInputStream().let {
    if (connection.contentEncoding?.contains("gzip") == true) {
      GZIPInputStream(it)
    } else {
      it
    }
  }.readBytes().toUTF8String()
}

fun sendGet(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
): String {
  val paramSB = StringBuilder()
  return sendGet(url, run {
    param.forEach {
      paramSB.append("${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}&")
    }
    paramSB.toString()
  }, headers)
}

fun sendGet(
  url: String,
  outputStream: OutputStream,
  param: String? = null,
  headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
) {
  val realUrl = URL(if (param != null) {
    "$url?$param"
  } else {
    url
  })
  // 打开和URL之间的连接
  val connection = realUrl.openConnection()
  // 设置请求属性
  headers.forEach {
    connection.addRequestProperty(it.key, it.value)
  }
  // 建立实际的连接
  connection.connect()

  // 读取URL的响应
  connection.getInputStream().use {
    it.copyTo(outputStream)
  }
}

fun sendGet(
  url: String,
  outputStream: OutputStream,
  param: Map<String, String>,
  headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
) {
  val paramSB = StringBuilder()
  sendGet(url, outputStream, run {
    param.forEach {
      paramSB.append("${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}&")
    }
    paramSB.toString()
  }, headers)
}

fun getFile(url: String, filename: String) {
  File(filename).outputStream().use {
    sendGet(url, it)
  }
}

fun sendHead(
  url: String,
  param: String,
  headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
): Map<String, List<String>> {
  val urlNameString = "$url?$param"
  val realUrl = URL(urlNameString)
  // 打开和URL之间的连接
  val connection = realUrl.openConnection() as HttpURLConnection
  // 设置请求属性
  headers.forEach {
    connection.addRequestProperty(it.key, it.value)
  }
  connection.requestMethod = "HEAD"
  // 建立实际的连接
  connection.connect()
  // 获取响应头字段
  return connection.headerFields
}

fun sendHead(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
): Map<String, List<String>> {
  val paramSB = StringBuilder()
  return sendHead(url, run {
    param.forEach {
      paramSB.append("${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}&")
    }
    paramSB.toString()
  }, headers)
}


/**
 * 向指定 URL 发送POST方法的请求
 *
 * @param url
 * 发送请求的 URL
 * @param data
 * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
 * @return 所代表远程资源的响应结果
 */
@Throws(Exception::class)
fun sendPost(
  url: String,
  data: ByteArray,
  headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
): String {
  val realUrl = URL(url)
  // 打开和URL之间的连接
  val conn = realUrl.openConnection()
  // 设置通用的请求属性
  headers.forEach { key, value ->
    conn.setRequestProperty(key, value)
  }
  // 发送POST请求必须设置如下两行，HttpUrlConnection会将请求方法自动设置为POST
  conn.doOutput = true
  conn.doInput = true

  // 获取URLConnection对象对应的输出流
  conn.outputStream.use { out ->
    // 发送请求参数
    out.write(data)
    // flush输出流的缓冲
    out.flush()
  }

  // 定义BufferedReader输入流来读取URL的响应
  return conn.getInputStream().readBytes().toUTF8String()
}

@Throws(Exception::class)
fun sendPost(
  url: String,
  param: Map<String, String>,
  headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
): String {
  val sb = StringBuilder()
  param.forEach { key, value ->
    sb.append("${URLEncoder.encode(key, "utf-8")}=${URLEncoder.encode(value, "utf-8")}&")
  }
  sb.deleteCharAt(sb.length - 1)
  return sendPost(url, sb.toString().toByteArray(), headers)
}

@Throws(Exception::class)
fun sendPost(
  url: String,
  outputStream: OutputStream,
  param: ByteArray,
  headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
) {
  val realUrl = URL(url)
  // 打开和URL之间的连接
  val conn = realUrl.openConnection()
  // 设置通用的请求属性
  headers.forEach { (key, value) ->
    conn.setRequestProperty(key, value)
  }
  // 发送POST请求必须设置如下两行，HttpUrlConnection会将请求方法自动设置为POST
  conn.doOutput = true
  conn.doInput = true

  // 获取URLConnection对象对应的输出流
  conn.outputStream.use { out ->
    // 发送请求参数
    out.write(param)
    // flush输出流的缓冲
    out.flush()
  }

  // 读取URL的响应
  conn.getInputStream().copyTo(outputStream)
}

@Throws(Exception::class)
fun sendPost(
  url: String,
  outputStream: OutputStream,
  param: Map<String, String>,
  headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
) {
  val sb = StringBuilder()
  param.forEach { (key, value) ->
    sb.append("${URLEncoder.encode(key, "utf-8")}=${URLEncoder.encode(value, "utf-8")}&")
  }
  sb.deleteCharAt(sb.length - 1)
  sendPost(url, outputStream, sb.toString().toByteArray(), headers)
}

@Suppress("MemberVisibilityCanBePrivate")
class HttpRequest(
  val url: String,
  val param: ByteArray? = null,
  val headers: Map<String, String> = mapOf(
    Pair("accept", "*/*"),
    Pair("connection", "Keep-Alive"),
    Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
) {

  constructor(
    url: String,
    param: Map<String, String>? = null,
    headers: Map<String, String> = mapOf(
      Pair("accept", "*/*"),
      Pair("connection", "Keep-Alive"),
      Pair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"))
  ) : this(
    url,
    param?.run {
      val sb = StringBuilder()
      param.forEach { (key, value) ->
        sb.append("${URLEncoder.encode(key, "utf-8")}=${URLEncoder.encode(value, "utf-8")}&")
      }
      sb.deleteCharAt(sb.length - 1)
      sb.toString().toByteArray()
    },
    headers)


  var body: String? = null
  var head: Map<String, List<String>>? = null

  @Throws(Exception::class)
  fun get() {
    body = null
    head = null
    val realUrl = URL(if (param != null) {
      "$url?${String(param)}"
    } else {
      url
    })
    // 打开和URL之间的连接
    val connection = realUrl.openConnection() as HttpURLConnection
    // 设置请求属性
    this.headers.forEach {
      connection.addRequestProperty(it.key, it.value)
    }
    // 建立实际的连接
    connection.connect()
    // 获取响应头字段
    head = connection.headerFields
    body = InputStreamReader(connection.inputStream).readText()
  }

  @Throws(Exception::class)
  fun post() {
    body = null
    head = null
    val realUrl = URL(url)
    // 打开和URL之间的连接
    val connection = realUrl.openConnection() as HttpURLConnection

    // 设置请求属性
    this.headers.forEach {
      connection.addRequestProperty(it.key, it.value)
    }

    // 发送POST请求必须设置如下两行，HttpUrlConnection会将请求方法自动设置为POST
    connection.doOutput = true
    connection.doInput = true

    // 建立实际的连接
    connection.connect()

    // 获取URLConnection对象对应的输出流
    if (param != null) connection.outputStream.use { out ->
      // 发送请求参数
      out.write(param)
      // flush输出流的缓冲
      out.flush()
    }

    // 获取响应头字段
    head = connection.headerFields
    body = InputStreamReader(connection.inputStream).readText()
  }
}