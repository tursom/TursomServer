package cn.tursom.ws

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.utils.bytebuffer.NettyByteBuffer
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

interface WebSocketHandler {
  fun onOpen(client: WebSocketClient) {}
  fun onClose(client: WebSocketClient) {}
  fun onError(client: WebSocketClient, e: Throwable) {
    throw e
  }

  fun readMessage(client: WebSocketClient, msg: String) {}
  fun readMessage(client: WebSocketClient, msg: TextWebSocketFrame) {
    readMessage(client, msg.text())
  }

  fun readMessage(client: WebSocketClient, msg: ByteArray) {}

  fun readMessage(client: WebSocketClient, msg: ByteBuf) {
    readMessage(client, NettyByteBuffer(msg))
  }

  fun readMessage(client: WebSocketClient, msg: ByteBuffer) {
    readMessage(client, msg.getBytes())
  }

  fun readMessage(client: WebSocketClient, msg: BinaryWebSocketFrame) {
    readMessage(client, msg.content())
  }
}