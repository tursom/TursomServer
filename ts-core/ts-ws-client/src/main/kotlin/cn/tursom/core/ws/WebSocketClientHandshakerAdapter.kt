package cn.tursom.core.ws

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker
import io.netty.util.CharsetUtil

class WebSocketClientHandshakerAdapter<T : WebSocketClient<T, H>, H : WebSocketHandler<T, H>>(
  private val handshaker: WebSocketClientHandshaker,
  private val client: T,
  private val handler: WebSocketHandler<T, H>,
) : SimpleChannelInboundHandler<FullHttpResponse>() {
  private var handshakeFuture: ChannelPromise? = null

  override fun handlerAdded(ctx: ChannelHandlerContext) {
    handshakeFuture = ctx.newPromise()
  }

  override fun channelActive(ctx: ChannelHandlerContext) {
    client.ch = ctx.channel()
    handshaker.handshake(ctx.channel())
  }

  override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpResponse) {
    if (!handshaker.isHandshakeComplete) {
      handshaker.finishHandshake(ctx.channel(), msg)
      handshakeFuture!!.setSuccess()
      //msg.retain()
      //ctx.fireChannelRead(msg)
      client.onOpen()
      handler.onOpen(client)
      return
    } else {
      throw Exception(
        "Unexpected FullHttpResponse (getStatus=${msg.status()}, content=${
          msg.content().toString(CharsetUtil.UTF_8)
        })"
      )
    }
  }

  override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    try {
      handler.onError(client, cause)
    } catch (e: Exception) {
      e.printStackTrace()
      if (!handshakeFuture!!.isDone) {
        handshakeFuture!!.setFailure(cause)
      }
      ctx.close()
    }
  }
}