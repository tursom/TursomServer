package cn.tursom.ws

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.toUTF8String
import cn.tursom.utils.bytebuffer.NettyByteBuffer
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame
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

  fun readPing(client: WebSocketClient, msg: PingWebSocketFrame) {
    readPing(client, msg.content())
  }

  fun readPing(client: WebSocketClient, msg: ByteBuf) {
    readPing(client, NettyByteBuffer(msg))
  }

  fun readPing(client: WebSocketClient, msg: ByteBuffer) {
    readPing(client, msg.getBytes())
  }

  fun readPing(client: WebSocketClient, msg: ByteArray) {
    readPing(client, msg.toUTF8String())
  }

  fun readPing(client: WebSocketClient, msg: String) {
  }

  fun readPong(client: WebSocketClient, msg: PongWebSocketFrame) {
    readPong(client, msg.content())
  }

  fun readPong(client: WebSocketClient, msg: ByteBuf) {
    readPong(client, NettyByteBuffer(msg))
  }

  fun readPong(client: WebSocketClient, msg: ByteBuffer) {
    readPong(client, msg.getBytes())
  }

  fun readPong(client: WebSocketClient, msg: ByteArray) {
    readPong(client, msg.toUTF8String())
  }

  fun readPong(client: WebSocketClient, msg: String) {
  }
}