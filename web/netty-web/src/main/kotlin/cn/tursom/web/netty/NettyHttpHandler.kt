package cn.tursom.web.netty

import cn.tursom.web.HttpHandler
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponseStatus

@ChannelHandler.Sharable
class NettyHttpHandler(
  private val handler: HttpHandler<NettyHttpContent, NettyExceptionContent>
) : SimpleChannelInboundHandler<FullHttpRequest>() {

  override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest) {
    val handlerContext = NettyHttpContent(ctx, msg)
    handler.handle(handlerContext)
  }

  override fun channelReadComplete(ctx: ChannelHandlerContext) {
    super.channelReadComplete(ctx)
    ctx.flush()
  }

  override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    val content = NettyExceptionContent(ctx, cause)
    handler.exceptionCause(content)
    if (!content.finished) {
      content.responseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR
      content.finish()
    }
    ctx.close()
  }
}