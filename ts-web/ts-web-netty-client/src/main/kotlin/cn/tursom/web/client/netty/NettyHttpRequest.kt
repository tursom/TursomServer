package cn.tursom.web.client.netty

import cn.tursom.core.AsyncFile
import cn.tursom.core.Unsafe.setField
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.NettyByteBuffer
import cn.tursom.core.toStartWith
import cn.tursom.log.impl.Slf4jImpl
import cn.tursom.web.client.HttpRequest
import cn.tursom.web.client.HttpResponse
import cn.tursom.web.client.ParamType
import cn.tursom.web.client.ParamsHolder
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.*
import kotlinx.coroutines.channels.ReceiveChannel
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class NettyHttpRequest(
  private val pool: HttpConnectionPool,
  val scheme: String,
  val host: String,
  val port: Int,
  path: String,
) : HttpRequest<NettyHttpRequest>, ParamsHolder<NettyHttpRequest> {
  companion object : Slf4jImpl() {
    val http2 = HttpVersion.valueOf("HTTP/2.0")
    private fun HttpHeaders.defaultContentType(contentType: CharSequence) {
      defaultHeader(HttpHeaderNames.CONTENT_TYPE, contentType)
    }

    private fun HttpHeaders.defaultHeader(header: CharSequence, contentType: CharSequence) {
      if (this[header] == null) {
        this[header] = contentType
      }
    }

    init {
      http2.setField("bytes", "h2".toByteArray())
    }
  }

  private val portStr: String
    get() = if (port <= 0) {
      ""
    } else {
      ":$port"
    }

  private var request: DefaultHttpRequest = DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "")

  override var version: String
    get() = request.protocolVersion().text()
    set(value) {
      request.protocolVersion = if (value == "h2") {
        http2
      } else {
        HttpVersion.valueOf(value)
      }
    }
  var httpVersion: HttpVersion
    get() = request.protocolVersion()
    set(value) {
      request.protocolVersion = value
    }

  override var method: String
    get() = request.method().name()
    set(value) {
      request.method = HttpMethod.valueOf(value)
    }

  override var path: String = path.toStartWith('/')
    set(value) {
      field = value.toStartWith('/')
    }
  override var paramType: ParamType = if (method in setOf("POST", "PUT", "PATCH")) ParamType.WEB_FORM else ParamType.URL

  override val params = HashMap<String, MutableList<String>>()

  override val headers: HttpHeaders
    get() = request.headers()

  override fun addHeader(key: String, value: Any): NettyHttpRequest {
    request.headers().add(key, value.toString())
    return this
  }

  override fun addHeaders(headers: Map<String, Any>): NettyHttpRequest {
    super.addHeaders(headers)
    return this
  }

  private var bodyChannel: ReceiveChannel<ByteBuffer>? = null
  private var body: ByteBuf? = null
  override fun body(channel: ReceiveChannel<ByteBuffer>): NettyHttpRequest {
    bodyChannel = channel
    return this
  }

  override fun body(data: ByteBuffer): NettyHttpRequest {
    body = NettyByteBuffer.toByteBuf(data)
    return this
  }

  override fun body(file: File): NettyHttpRequest {
    // TODO
    headers.defaultContentType("")
    AsyncFile(file.path)
    return super.body(file)
  }

  override fun body(string: String): NettyHttpRequest {
    headers.defaultContentType("text/plain;charset=utf-8")
    return super.body(string)
  }

  override suspend fun send(): HttpResponse {
    val paramStr = paramStr
    request.uri = buildString {
      //append("$scheme://$host$portStr$path")
      append(path)
      if (paramType == ParamType.URL) {
        if (paramStr.isNotEmpty()) {
          append("&")
          append(paramStr)
        }
      }
    }
    if (paramType == ParamType.WEB_FORM && bodyChannel == null && body == null) {
      headers.defaultContentType("application/x-www-form-urlencoded")
      body(paramStr)
    }
    headers["Host"] = host
    if (headers["Accept-Encoding"] == null) {
      headers["Accept-Encoding"] = "gzip, deflate, br"
    }
    if (request.protocolVersion() != HttpVersion.HTTP_1_0) {
      headers["Connection"] = HttpHeaderValues.KEEP_ALIVE
    }
    if (bodyChannel == null && body != null) {
      request = DefaultFullHttpRequest(
        request.protocolVersion(), request.method(), request.uri(),
        body, request.headers(), DefaultHttpHeaders(true))
    }
    val receiveChannel = NettyHttpConnection.request(pool, request, bodyChannel)
    val httpResponse = receiveChannel.receive() as io.netty.handler.codec.http.HttpResponse
    return NettyHttpResponse(httpResponse, receiveChannel)
  }
}
