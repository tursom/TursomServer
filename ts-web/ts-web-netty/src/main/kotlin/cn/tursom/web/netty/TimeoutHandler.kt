package cn.tursom.web.netty

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.timeout.TimeoutException

@ChannelHandler.Sharable
object TimeoutHandler : ChannelHandler {
  override fun handlerAdded(ctx: ChannelHandlerContext?) {}
  override fun handlerRemoved(ctx: ChannelHandlerContext?) {}
  override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable?) {
    when (cause) {
      is TimeoutException -> ctx.close()
      else -> ctx.fireExceptionCaught(cause)
    }
  }
}