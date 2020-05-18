package cn.tursom.forward

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

interface NettyHandler {
  fun onOpen(ctx: ChannelHandlerContext) {
  }

  fun recvBytes(buf: ByteBuf, ctx: ChannelHandlerContext) {
  }

  fun onClose(ctx: ChannelHandlerContext) {
  }

  fun exceptionCaused(cause: Throwable, ctx: ChannelHandlerContext) {
    cause.printStackTrace()
    ctx.close()
  }
}