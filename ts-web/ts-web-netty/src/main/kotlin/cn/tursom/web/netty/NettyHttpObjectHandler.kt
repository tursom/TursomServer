package cn.tursom.web.netty

import cn.tursom.web.HttpHandler
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest
import io.netty.util.AttributeKey

@ChannelHandler.Sharable
class NettyHttpObjectHandler(
  private val handler: HttpHandler<NettyHttpContent, NettyExceptionContent>,
) : SimpleChannelInboundHandler<HttpObject>() {
  companion object {
    private val context = AttributeKey.newInstance<NettyHttpContent>("NettyHttpContent")
  }

  override fun channelRead0(ctx: ChannelHandlerContext, msg: HttpObject) {
    when (msg) {
      is HttpRequest -> {
        val newHandlerContext = NettyHttpContent(ctx, msg)
        ctx.channel().attr(context).set(newHandlerContext)
        handler(newHandlerContext)
      }
      is HttpContent -> {
        val content = ctx.channel().attr(context).get()
        content.newResponseBody(msg)
      }
      else -> {
        ctx.fireChannelRead(msg)
      }
    }
  }
}