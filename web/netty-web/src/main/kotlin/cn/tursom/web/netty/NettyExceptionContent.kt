package cn.tursom.web.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.log.traceEnabled
import cn.tursom.utils.bytebuffer.NettyByteBuffer
import cn.tursom.web.ExceptionContent
import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.*
import org.slf4j.LoggerFactory

@Suppress("MemberVisibilityCanBePrivate")
class NettyExceptionContent(
  val ctx: ChannelHandlerContext,
  override val cause: Throwable
) : ExceptionContent, NettyResponseHeaderAdapter() {
  override var finished: Boolean = false
  val responseBodyBuf: CompositeByteBuf = ctx.alloc().compositeBuffer()!!
  var responseStatus: HttpResponseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR
  override var responseCode: Int
    get() = responseStatus.code()
    set(value) {
      responseStatus = HttpResponseStatus.valueOf(value)
    }

  override fun write(message: String) {
    if (log.traceEnabled) {
      log?.trace("write message: {}", message)
    }
    responseBodyBuf.addComponent(Unpooled.wrappedBuffer(message.toByteArray()))
  }

  override fun write(bytes: ByteArray, offset: Int, length: Int) {
    if (log.traceEnabled) {
      log?.trace("write bytes: {}", String(bytes, offset, length, Charsets.UTF_8))
    }
    responseBodyBuf.addComponent(Unpooled.wrappedBuffer(bytes, offset, length))
  }

  override fun write(buffer: ByteBuffer) {
    if (log.traceEnabled) {
      log?.trace("write buffer: {}", buffer.toString(buffer.readable))
    }
    when (buffer) {
      is NettyByteBuffer -> responseBodyBuf.addComponent(buffer.byteBuf)
      else -> write(buffer.getBytes())
    }
  }

  override fun finish() {
    finish(responseBodyBuf)
  }

  fun finish(buf: ByteBuf) = finish(buf, responseStatus)
  fun finish(buf: ByteBuf, responseCode: HttpResponseStatus) {
    if (log.traceEnabled) {
      log?.trace("finish buf: {}, responseCode: {}", buf.toString(Charsets.UTF_8), responseCode)
    }
    val response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseCode, buf)
    finish(response)
  }

  fun finish(response: FullHttpResponse) {
    if (log.traceEnabled) {
      log?.trace("finish")
    }
    finished = true
    response.headers().addHeaders(
      HttpHeaderNames.CONTENT_TYPE to "${HttpHeaderValues.TEXT_PLAIN}; charset=UTF-8",
      HttpHeaderNames.CONTENT_LENGTH to response.content().readableBytes(),
      HttpHeaderNames.CONNECTION to HttpHeaderValues.KEEP_ALIVE
    )
    ctx.writeAndFlush(response)
  }

  companion object {
    private val log = try {
      LoggerFactory.getLogger(NettyExceptionContent::class.java)
    } catch (e: Throwable) {
      null
    }
  }
}