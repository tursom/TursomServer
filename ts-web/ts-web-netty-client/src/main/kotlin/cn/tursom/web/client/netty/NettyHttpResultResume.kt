package cn.tursom.web.client.netty

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.LastHttpContent
import io.netty.util.AttributeKey
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.trySendBlocking
import java.util.concurrent.atomic.AtomicInteger

@ChannelHandler.Sharable
object NettyHttpResultResume : SimpleChannelInboundHandler<HttpObject>() {
  val recvChannelKey = AttributeKey.newInstance<SendChannel<HttpObject>>("recvChannelKey")!!
  val countKey = AttributeKey.newInstance<AtomicInteger>("countKey")!!
  override fun channelRead0(ctx: ChannelHandlerContext, msg: HttpObject) {
    val channel = ctx.channel().attr(recvChannelKey).get() ?: return
    val send = channel.trySendBlocking(msg)
    if (send.isFailure || msg is LastHttpContent) {
      channel.close()
      ctx.channel().attr(recvChannelKey).set(null)
    }
  }

  @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
  override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    val channel = ctx.channel().attr(recvChannelKey).get()
    if (channel == null) {
      super.exceptionCaught(ctx, cause)
      return
    }
    channel.close(cause)
  }

  override fun channelInactive(ctx: ChannelHandlerContext) {
    ctx.channel().attr(countKey).get()?.decrementAndGet()
    super.channelInactive(ctx)
  }
}
