package cn.tursom.forward.datagram

import cn.tursom.forward.NettyHandler
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class DatagramServerHandler(private val handler: NettyHandler) : SimpleChannelInboundHandler<ByteBuf>() {
  override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
    handler.recvBytes(msg, ctx)
  }

  override fun channelInactive(ctx: ChannelHandlerContext) {
    handler.onClose(ctx)
    super.channelInactive(ctx)
  }

  override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    handler.exceptionCaused(cause, ctx)
  }

  override fun channelActive(ctx: ChannelHandlerContext) {
    handler.onOpen(ctx)
    super.channelActive(ctx)
  }
}