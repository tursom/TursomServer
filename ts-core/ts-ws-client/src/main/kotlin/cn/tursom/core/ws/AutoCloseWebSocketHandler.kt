package cn.tursom.core.ws

import cn.tursom.core.buffer.ByteBuffer
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

interface AutoCloseWebSocketHandler<in T : WebSocketClient<T, H>, H : WebSocketHandler<T, H>> : WebSocketHandler<T, H> {
  override fun readMessage(client: T, msg: TextWebSocketFrame) {
    super.readMessage(client, msg)
    msg.release()
  }

  override fun readMessage(client: T, msg: ByteBuffer) {
    super.readMessage(client, msg)
    msg.close()
  }

  override fun readPing(client: T, msg: ByteBuffer) {
    super.readPing(client, msg)
    msg.close()
  }

  override fun readPong(client: T, msg: ByteBuffer) {
    super.readPong(client, msg)
    msg.close()
  }
}