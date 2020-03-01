package cn.tursom.ws

import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker
import io.netty.util.CharsetUtil



class WebSocketClientChannelHandler(
  private val handshaker: WebSocketClientHandshaker,
  val client: WebSocketClient,
  val handler: WebSocketHandler
) : SimpleChannelInboundHandler<Any>() {
  private var handshakeFuture: ChannelPromise? = null

  fun handshakeFuture(): ChannelFuture? {
    return handshakeFuture
  }

  override fun handlerAdded(ctx: ChannelHandlerContext) {
    handshakeFuture = ctx.newPromise()
  }

  override fun channelActive(ctx: ChannelHandlerContext) {
    handshaker.handshake(ctx.channel())
  }

  override fun channelInactive(ctx: ChannelHandlerContext) {
    handler.onClose(client)
  }

  override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
    val ch = ctx.channel()
    if (!handshaker.isHandshakeComplete) { // web socket client connected
      handshaker.finishHandshake(ch, msg as FullHttpResponse)
      handshakeFuture!!.setSuccess()
      handler.onOpen(client)
      return
    }
    if (msg is FullHttpResponse) {
      throw Exception("Unexpected FullHttpResponse (getStatus=${msg.status()}, content=${msg.content().toString(CharsetUtil.UTF_8)})")
    }
    when (msg) {
      is TextWebSocketFrame -> handler.readMessage(client, msg)
      is BinaryWebSocketFrame -> handler.readMessage(client, msg)
      is CloseWebSocketFrame -> ch.close()
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