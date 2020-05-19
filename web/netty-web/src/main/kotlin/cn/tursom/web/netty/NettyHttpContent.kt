package cn.tursom.web.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.utils.bytebuffer.NettyByteBuffer
import cn.tursom.web.MutableHttpContent
import cn.tursom.web.utils.Chunked
import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import io.netty.handler.stream.ChunkedFile
import org.slf4j.LoggerFactory
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.set

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class NettyHttpContent(
  val ctx: ChannelHandlerContext,
  val request: HttpRequest
) : MutableHttpContent, NettyResponseHeaderAdapter() {
  val decoder = HttpPostRequestDecoder(request)
  override var requestSendFully: Boolean = false
  override var finished: Boolean = false
  override val uri: String by lazy {
    var uri = request.uri()
    while (uri.contains("//")) {
      uri = uri.replace("//", "/")
    }
    uri
  }
  override val clientIp get() = ctx.channel().remoteAddress()!!
  override val realIp: String = super.realIp
  val httpMethod: HttpMethod get() = request.method()
  val protocolVersion: HttpVersion get() = request.protocolVersion()
  val headers: HttpHeaders get() = request.headers()
  protected val paramMap by lazy { ParamParser.parse(request) }
  override val cookieMap by lazy { getHeader("Cookie")?.let { decodeCookie(it) } ?: mapOf() }

  private var waitBodyHandler = ConcurrentLinkedQueue<(end: Boolean) -> Unit>()
  override val body: ByteBuffer? get() = bodyList.poll()?.content()?.let { NettyByteBuffer(it) }
  val bodyList = ConcurrentLinkedQueue<HttpContent>()

  //override val responseBody = ByteArrayOutputStream()
  var responseStatus: HttpResponseStatus = HttpResponseStatus.OK
  override var responseCode: Int
    get() = responseStatus.code()
    set(value) {
      responseStatus = HttpResponseStatus.valueOf(value)
    }
  override var responseMessage: String? = null
  override val method: String get() = httpMethod.name()
  val chunkedList = ArrayList<() -> ByteBuffer>()
  private var responseBodyBuf: CompositeByteBuf? = null

  fun newResponseBody(httpContent: HttpContent) {
    bodyList.add(httpContent)
    val end = if (httpContent is LastHttpContent) {
      requestSendFully = true
      true
    } else {
      false
    }
    while (waitBodyHandler.isNotEmpty()) {
      val handler = waitBodyHandler.poll() ?: continue
      handler(end)
    }
  }

  override fun waitBody(action: (end: Boolean) -> Unit) {
    if (!requestSendFully) {
      waitBodyHandler.add(action)
    }
  }

  override fun addBodyParam() {
    ParamParser.parse(request, bodyList.poll()!!, paramMap)
  }

  override fun addBodyParam(body: ByteBuffer) {
    val byteBuf = if (body is NettyByteBuffer) {
      body.byteBuf
    } else {
      Unpooled.wrappedBuffer(body.readBuffer())
    }
    ParamParser.parse(request, DefaultHttpContent(byteBuf), paramMap)
    body.close()
  }

  fun getResponseBodyBuf(): CompositeByteBuf {
    if (responseBodyBuf == null) {
      responseBodyBuf = ctx.alloc().compositeBuffer()!!
    }
    return responseBodyBuf!!
  }

  override fun getHeader(header: String): String? {
    log?.trace("getHeader {}", header)
    return headers[header]
  }

  override fun getHeaders(header: String): List<String> {
    log?.trace("getHeaders {}", header)
    return headers.getAll(header)
  }

  override fun getHeaders(): Iterable<Map.Entry<String, String>> {
    log?.trace("getHeaders")
    return headers
  }

  override fun getParams(): Map<String, List<String>> {
    log?.trace("getParams")
    return paramMap
  }

  override fun getParams(param: String): List<String>? {
    log?.trace("getParams {}", param)
    return paramMap[param]
  }

  override fun addParam(key: String, value: String) {
    log?.trace("addParam {}: {}", key, value)
    if (!paramMap.containsKey(key)) {
      paramMap[key] = ArrayList()
    }
    (paramMap[key] as ArrayList).add(value)
  }

  override fun write(message: String) {
    log?.trace("write {}", message)
    getResponseBodyBuf().addComponent(Unpooled.wrappedBuffer(message.toByteArray()))
    //responseBody.write(message.toByteArray())
  }

  override fun write(byte: Byte) {
    log?.trace("write {}", byte)
    val buffer = ctx.alloc().buffer(1).writeByte(byte.toInt())
    getResponseBodyBuf().addComponent(buffer)
    //responseBody.write(byte.toInt())
  }

  override fun write(bytes: ByteArray, offset: Int, size: Int) {
    log?.trace("write {}({}:{})", bytes, offset, size)
    getResponseBodyBuf().addComponent(Unpooled.wrappedBuffer(bytes, offset, size))
    //responseBody.write(bytes, offset, size)
  }

  override fun write(buffer: ByteBuffer) {
    //buffer.writeTo(responseBody)
    log?.trace("write {}", buffer)
    getResponseBodyBuf().addComponent(
      if (buffer is NettyByteBuffer) {
        buffer.byteBuf
      } else {
        val buf = Unpooled.wrappedBuffer(buffer.readBuffer())
        buffer.clear()
        buf
      }
    )
  }

  override fun reset() {
    log?.trace("reset")
    getResponseBodyBuf().clear()
  }

  override fun finish() {
    log?.trace("finish")
    finish(getResponseBodyBuf())
  }

  override fun finish(buffer: ByteArray, offset: Int, size: Int) {
    log?.trace("finish ByteArray[{}]({}:{})", buffer.size, offset, size)
    finish(Unpooled.wrappedBuffer(buffer, offset, size))
  }

  override fun finish(buffer: ByteBuffer) {
    log?.trace("finish {}", buffer)
    if (buffer is NettyByteBuffer) {
      finish(buffer.byteBuf)
    } else {
      super.finish(buffer)
    }
  }

  fun finish(buf: ByteBuf) = finish(buf, responseStatus)
  fun finish(buf: ByteBuf, responseCode: HttpResponseStatus): ChannelFuture {
    log?.trace("finish {}: {}", responseCode, buf)
    val response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseCode, buf)
    return finish(response)
  }

  fun finish(response: FullHttpResponse): ChannelFuture {
    log?.trace("finish {}", response)
    finished = true
    val heads = response.headers()
    addHeaders(
      heads,
      mapOf(
        HttpHeaderNames.CONTENT_TYPE to "${HttpHeaderValues.TEXT_PLAIN}; charset=UTF-8",
        HttpHeaderNames.CONTENT_LENGTH to response.content().readableBytes(),
        HttpHeaderNames.CONNECTION to HttpHeaderValues.KEEP_ALIVE
      )
    )
    val write = ctx.writeAndFlush(response)
    write.addListener {
      if (it.isDone) {
        val bodyBuf = responseBodyBuf ?: return@addListener
        bodyBuf.release(bodyBuf.refCnt())
      }
    }
    return write
  }

  override fun writeChunkedHeader() {
    log?.trace("writeChunkedHeader")
    val response = DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    response.status = if (responseMessage != null) HttpResponseStatus(responseCode, responseMessage)
    else responseStatus
    val heads = response.headers()
    addHeaders(
      heads,
      mapOf(
        HttpHeaderNames.CONTENT_TYPE to "${HttpHeaderValues.TEXT_PLAIN}; charset=UTF-8",
        HttpHeaderNames.CONNECTION to HttpHeaderValues.KEEP_ALIVE,
        HttpHeaderNames.TRANSFER_ENCODING to "chunked"
      )
    )
    ctx.write(response)
  }

  override fun addChunked(buffer: () -> ByteBuffer) {
    log?.trace("addChunked {}", buffer)
    chunkedList.add(buffer)
  }

  override fun finishChunked() {
    log?.trace("finishChunked {}", chunkedList)
    finished = true
    responseBodyBuf?.release()
    writeChunkedHeader()
    val httpChunkWriter = HttpChunkedInput(NettyChunkedByteBuffer(chunkedList))
    ctx.writeAndFlush(httpChunkWriter)
  }

  override fun finishChunked(chunked: Chunked) {
    log?.trace("finishChunked {}", chunked)
    finished = true
    responseBodyBuf?.release()
    writeChunkedHeader()
    val httpChunkWriter = HttpChunkedInput(NettyChunkedInput(chunked))
    ctx.writeAndFlush(httpChunkWriter)
  }

  override fun finishFile(file: File, chunkSize: Int) {
    log?.trace("finishFile {} chunkSize {}", file, chunkSize)
    finished = true
    responseBodyBuf?.release()
    writeChunkedHeader()
    ctx.writeAndFlush(HttpChunkedInput(ChunkedFile(file, chunkSize)))
  }

  override fun finishFile(file: RandomAccessFile, offset: Long, length: Long, chunkSize: Int) {
    log?.trace("finishFile {}({}:{}) chunkSize {}", file, offset, length, chunkSize)
    finished = true
    responseBodyBuf?.release()
    writeChunkedHeader()
    ctx.writeAndFlush(HttpChunkedInput(ChunkedFile(file, offset, length, chunkSize)))
  }

  companion object {
    private val log = try {
      LoggerFactory.getLogger(NettyHttpContent::class.java)
    } catch (e: Throwable) {
      null
    }
  }
}

