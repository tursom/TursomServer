package cn.tursom.web.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.web.ExceptionContent
import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.*

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
    responseBodyBuf.addComponent(Unpooled.wrappedBuffer(message.toByteArray()))
  }

  override fun write(bytes: ByteArray, offset: Int, length: Int) {
    responseBodyBuf.addComponent(Unpooled.wrappedBuffer(bytes, offset, length))
  }

  override fun write(buffer: ByteBuffer) {
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
    val response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseCode, buf)
    finish(response)
  }

  fun finish(response: FullHttpResponse) {
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
    ctx.writeAndFlush(response)
  }
}