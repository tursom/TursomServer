package cn.tursom.core.ws

import cn.tursom.core.buffer.ByteBuffer
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

@Suppress("unused")
open class AbstractWebSocketHandler : WebSocketHandler {
  private var onOpen: ((client: WebSocketClient) -> Unit)? = null

  fun onOpen(onOpen: ((client: WebSocketClient) -> Unit)) {
    this.onOpen = onOpen
  }

  override fun onOpen(client: WebSocketClient) {
    onOpen?.also { it(client) } ?: super.onOpen(client)
  }

  private var onClose: ((client: WebSocketClient) -> Unit)? = null

  fun onClose(onClose: ((client: WebSocketClient) -> Unit)) {
    this.onClose = onClose
  }

  override fun onClose(client: WebSocketClient) {
    onClose?.also { it(client) } ?: super.onClose(client)
  }

  private var onError: ((client: WebSocketClient, e: Throwable) -> Unit)? = null

  fun onError(onError: ((client: WebSocketClient, e: Throwable) -> Unit)) {
    this.onError = onError
  }

  override fun onError(client: WebSocketClient, e: Throwable) {
    onError?.also { it(client, e) } ?: super.onError(client, e)
  }

  private var readMessage1: ((client: WebSocketClient, msg: String) -> Unit)? = null

  @JvmName("readMessage1")
  fun readMessage(readMessage: (client: WebSocketClient, msg: String) -> Unit) {
    readMessage1 = readMessage
  }

  override fun readMessage(client: WebSocketClient, msg: String) {
    readMessage1?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readMessage2: ((client: WebSocketClient, msg: TextWebSocketFrame) -> Unit)? = null

  @JvmName("readMessage2")
  fun readMessage(readMessage: (client: WebSocketClient, msg: TextWebSocketFrame) -> Unit) {
    readMessage2 = readMessage
  }

  override fun readMessage(client: WebSocketClient, msg: TextWebSocketFrame) {
    readMessage2?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readMessage3: ((client: WebSocketClient, msg: ByteArray) -> Unit)? = null

  @JvmName("readMessage3")
  fun readMessage(readMessage: (client: WebSocketClient, msg: ByteArray) -> Unit) {
    readMessage3 = readMessage
  }

  override fun readMessage(client: WebSocketClient, msg: ByteArray) {
    readMessage3?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readMessage4: ((client: WebSocketClient, msg: ByteBuf) -> Unit)? = null

  @JvmName("readMessage4")
  fun readMessage(readMessage: (client: WebSocketClient, msg: ByteBuf) -> Unit) {
    readMessage4 = readMessage
  }

  override fun readMessage(client: WebSocketClient, msg: ByteBuf) {
    readMessage4?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readMessage5: ((client: WebSocketClient, msg: ByteBuffer) -> Unit)? = null

  @JvmName("readMessage5")
  fun readMessage(readMessage: (client: WebSocketClient, msg: ByteBuffer) -> Unit) {
    readMessage5 = readMessage
  }

  override fun readMessage(client: WebSocketClient, msg: ByteBuffer) {
    readMessage5?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readMessage6: ((client: WebSocketClient, msg: BinaryWebSocketFrame) -> Unit)? = null

  @JvmName("readMessage6")
  fun readMessage(readMessage: (client: WebSocketClient, msg: BinaryWebSocketFrame) -> Unit) {
    readMessage6 = readMessage
  }

  override fun readMessage(client: WebSocketClient, msg: BinaryWebSocketFrame) {
    readMessage6?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readPing1: ((client: WebSocketClient, msg: PingWebSocketFrame) -> Unit)? = null

  @JvmName("readPing1")
  fun readPing(readMessage: (client: WebSocketClient, msg: PingWebSocketFrame) -> Unit) {
    readPing1 = readMessage
  }

  override fun readPing(client: WebSocketClient, msg: PingWebSocketFrame) {
    readPing1?.also { it(client, msg) } ?: super.readPing(client, msg)
  }

  private var readPing2: ((client: WebSocketClient, msg: ByteBuf) -> Unit)? = null

  @JvmName("readPing2")
  fun readPing(readMessage: (client: WebSocketClient, msg: ByteBuf) -> Unit) {
    readPing2 = readMessage
  }

  override fun readPing(client: WebSocketClient, msg: ByteBuf) {
    readPing2?.also { it(client, msg) } ?: super.readPing(client, msg)
  }

  private var readPing3: ((client: WebSocketClient, msg: ByteBuffer) -> Unit)? = null

  @JvmName("readPing3")
  fun readPing(readMessage: (client: WebSocketClient, msg: ByteBuffer) -> Unit) {
    readPing3 = readMessage
  }

  override fun readPing(client: WebSocketClient, msg: ByteBuffer) {
    readPing3?.also { it(client, msg) } ?: super.readPing(client, msg)
  }

  private var readPing4: ((client: WebSocketClient, msg: ByteArray) -> Unit)? = null

  @JvmName("readPing4")
  fun readPing(readMessage: (client: WebSocketClient, msg: ByteArray) -> Unit) {
    readPing4 = readMessage
  }

  override fun readPing(client: WebSocketClient, msg: ByteArray) {
    readPing4?.also { it(client, msg) } ?: super.readPing(client, msg)
  }

  private var readPing5: ((client: WebSocketClient, msg: String) -> Unit)? = null

  @JvmName("readPing5")
  fun readPing(readMessage: (client: WebSocketClient, msg: String) -> Unit) {
    readPing5 = readMessage
  }

  override fun readPing(client: WebSocketClient, msg: String) {
    readPing5?.also { it(client, msg) } ?: super.readPing(client, msg)
  }

  private var readPong1: ((client: WebSocketClient, msg: PongWebSocketFrame) -> Unit)? = null

  @JvmName("readPong1")
  fun readPong(readMessage: (client: WebSocketClient, msg: PongWebSocketFrame) -> Unit) {
    readPong1 = readMessage
  }

  override fun readPong(client: WebSocketClient, msg: PongWebSocketFrame) {
    readPong1?.also { it(client, msg) } ?: super.readPong(client, msg)
  }

  private var readPong2: ((client: WebSocketClient, msg: ByteBuf) -> Unit)? = null

  @JvmName("readPong2")
  fun readPong(readMessage: (client: WebSocketClient, msg: ByteBuf) -> Unit) {
    readPong2 = readMessage
  }

  override fun readPong(client: WebSocketClient, msg: ByteBuf) {
    readPong2?.also { it(client, msg) } ?: super.readPong(client, msg)
  }

  private var readPong3: ((client: WebSocketClient, msg: ByteBuffer) -> Unit)? = null

  @JvmName("readPong3")
  fun readPong(readMessage: (client: WebSocketClient, msg: ByteBuffer) -> Unit) {
    readPong3 = readMessage
  }

  override fun readPong(client: WebSocketClient, msg: ByteBuffer) {
    readPong3?.also { it(client, msg) } ?: super.readPong(client, msg)
  }

  private var readPong4: ((client: WebSocketClient, msg: ByteArray) -> Unit)? = null

  @JvmName("readPong4")
  fun readPong(readMessage: (client: WebSocketClient, msg: ByteArray) -> Unit) {
    readPong4 = readMessage
  }

  override fun readPong(client: WebSocketClient, msg: ByteArray) {
    readPong4?.also { it(client, msg) } ?: super.readPong(client, msg)
  }

  private var readPong5: ((client: WebSocketClient, msg: String) -> Unit)? = null

  @JvmName("readPong5")
  fun readPong(readMessage: (client: WebSocketClient, msg: String) -> Unit) {
    readPong5 = readMessage
  }

  override fun readPong(client: WebSocketClient, msg: String) {
    readPong5?.also { it(client, msg) } ?: super.readPong(client, msg)
  }
}