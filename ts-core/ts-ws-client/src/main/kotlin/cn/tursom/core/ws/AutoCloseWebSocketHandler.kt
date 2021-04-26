package cn.tursom.core.ws

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

interface AutoCloseWebSocketHandler : WebSocketHandler {
  override fun readMessage(client: WebSocketClient, msg: TextWebSocketFrame) {
    super.readMessage(client, msg)
    msg.release()
  }

  override fun readMessage(client: WebSocketClient, msg: ByteBuf) {
    super.readMessage(client, msg)
    msg.release()
  }

  override fun readPing(client: WebSocketClient, msg: ByteBuf) {
    super.readPing(client, msg)
    msg.release()
  }

  override fun readPong(client: WebSocketClient, msg: ByteBuf) {
    super.readPong(client, msg)
    msg.release()
  }
}