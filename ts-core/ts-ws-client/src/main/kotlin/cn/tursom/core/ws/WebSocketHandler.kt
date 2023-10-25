package cn.tursom.core.ws

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.NettyByteBuffer
import cn.tursom.core.util.toUTF8String
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

interface WebSocketHandler<in T : WebSocketClient<T, in H>, in H : WebSocketHandler<T, H>> {
  fun onOpen(client: T) {}
  fun onClose(client: T) {}
  fun onError(client: T, e: Throwable) {
    throw e
  }

  fun readMessage(client: T, msg: String) {}
  fun readMessage(client: T, msg: TextWebSocketFrame) {
    readMessage(client, msg.text())
  }

  fun readMessage(client: T, msg: ByteArray) {}

  fun readMessage(client: T, msg: ByteBuf) {
    readMessage(client, NettyByteBuffer(msg))
  }

  fun readMessage(client: T, msg: ByteBuffer) {
    readMessage(client, msg.getBytes())
  }

  fun readMessage(client: T, msg: BinaryWebSocketFrame) {
    readMessage(client, msg.content())
  }

  fun readPing(client: T, msg: PingWebSocketFrame) {
    readPing(client, msg.content())
  }

  fun readPing(client: T, msg: ByteBuf) {
    readPing(client, NettyByteBuffer(msg))
  }

  fun readPing(client: T, msg: ByteBuffer) {
    readPing(client, msg.getBytes())
  }

  fun readPing(client: T, msg: ByteArray) {
    readPing(client, msg.toUTF8String())
  }

  fun readPing(client: T, msg: String) {
  }

  fun readPong(client: T, msg: PongWebSocketFrame) {
    readPong(client, msg.content())
  }

  fun readPong(client: T, msg: ByteBuf) {
    readPong(client, NettyByteBuffer(msg))
  }

  fun readPong(client: T, msg: ByteBuffer) {
    readPong(client, msg.getBytes())
  }

  fun readPong(client: T, msg: ByteArray) {
    readPong(client, msg.toUTF8String())
  }

  fun readPong(client: T, msg: String) {
  }
}