package cn.tursom.forward.ws

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.nio.charset.StandardCharsets

interface WebSocketHandler {
  fun onOpen(ctx: ChannelHandlerContext) {
  }

  fun recvStr(str: String, ctx: ChannelHandlerContext) {
  }

  fun recvStr(str: ByteBuf, ctx: ChannelHandlerContext) {
    recvStr(str.toString(StandardCharsets.UTF_8), ctx)
  }

  fun recvBytes(bytes: ByteArray, ctx: ChannelHandlerContext) {
  }

  fun recvBytes(buf: ByteBuf, ctx: ChannelHandlerContext) {
    val buffer = ByteArray(buf.readableBytes())
    buf.readBytes(buffer)
    recvBytes(buffer, ctx)
  }

  fun onClose(ctx: ChannelHandlerContext) {
  }

  fun exceptionCaused(cause: Throwable, ctx: ChannelHandlerContext) {
    cause.printStackTrace()
    ctx.close()
  }
}