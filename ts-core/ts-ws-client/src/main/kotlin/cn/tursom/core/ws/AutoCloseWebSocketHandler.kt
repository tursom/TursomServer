package cn.tursom.core.ws

import cn.tursom.core.buffer.ByteBuffer
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

interface AutoCloseWebSocketHandler : WebSocketHandler {
  override fun readMessage(client: WebSocketClient, msg: TextWebSocketFrame) {
    super.readMessage(client, msg)
    msg.release()
  }

  override fun readMessage(client: WebSocketClient, msg: ByteBuffer) {
    super.readMessage(client, msg)
    msg.close()
  }

  override fun readPing(client: WebSocketClient, msg: ByteBuffer) {
    super.readPing(client, msg)
    msg.close()
  }

  override fun readPong(client: WebSocketClient, msg: ByteBuffer) {
    super.readPong(client, msg)
    msg.close()
  }
}