package cn.tursom.core.ws

import cn.tursom.core.buffer.ByteBuffer
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

@Suppress("unused")
open class AbstractWebSocketHandler<T : WebSocketClient<T, H>, H : WebSocketHandler<T, H>> : WebSocketHandler<T, H> {
  private var onOpen: ((client: T) -> Unit)? = null

  fun onOpen(onOpen: ((client: T) -> Unit)) {
    this.onOpen = onOpen
  }

  override fun onOpen(client: T) {
    onOpen?.also { it(client) } ?: super.onOpen(client)
  }

  private var onClose: ((client: T) -> Unit)? = null

  fun onClose(onClose: ((client: T) -> Unit)) {
    this.onClose = onClose
  }

  override fun onClose(client: T) {
    onClose?.also { it(client) } ?: super.onClose(client)
  }

  private var onError: ((client: T, e: Throwable) -> Unit)? = null

  fun onError(onError: ((client: T, e: Throwable) -> Unit)) {
    this.onError = onError
  }

  override fun onError(client: T, e: Throwable) {
    onError?.also { it(client, e) } ?: super.onError(client, e)
  }

  private var readMessage1: ((client: T, msg: String) -> Unit)? = null

  @JvmName("readMessage1")
  fun readMessage(readMessage: (client: T, msg: String) -> Unit) {
    readMessage1 = readMessage
  }

  override fun readMessage(client: T, msg: String) {
    readMessage1?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readMessage2: ((client: T, msg: TextWebSocketFrame) -> Unit)? = null

  @JvmName("readMessage2")
  fun readMessage(readMessage: (client: T, msg: TextWebSocketFrame) -> Unit) {
    readMessage2 = readMessage
  }

  override fun readMessage(client: T, msg: TextWebSocketFrame) {
    readMessage2?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readMessage3: ((client: T, msg: ByteArray) -> Unit)? = null

  @JvmName("readMessage3")
  fun readMessage(readMessage: (client: T, msg: ByteArray) -> Unit) {
    readMessage3 = readMessage
  }

  override fun readMessage(client: T, msg: ByteArray) {
    readMessage3?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readMessage4: ((client: T, msg: ByteBuf) -> Unit)? = null

  @JvmName("readMessage4")
  fun readMessage(readMessage: (client: T, msg: ByteBuf) -> Unit) {
    readMessage4 = readMessage
  }

  override fun readMessage(client: T, msg: ByteBuf) {
    readMessage4?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readMessage5: ((client: T, msg: ByteBuffer) -> Unit)? = null

  @JvmName("readMessage5")
  fun readMessage(readMessage: (client: T, msg: ByteBuffer) -> Unit) {
    readMessage5 = readMessage
  }

  override fun readMessage(client: T, msg: ByteBuffer) {
    readMessage5?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readMessage6: ((client: T, msg: BinaryWebSocketFrame) -> Unit)? = null

  @JvmName("readMessage6")
  fun readMessage(readMessage: (client: T, msg: BinaryWebSocketFrame) -> Unit) {
    readMessage6 = readMessage
  }

  override fun readMessage(client: T, msg: BinaryWebSocketFrame) {
    readMessage6?.also { it(client, msg) } ?: super.readMessage(client, msg)
  }

  private var readPing1: ((client: T, msg: PingWebSocketFrame) -> Unit)? = null

  @JvmName("readPing1")
  fun readPing(readMessage: (client: T, msg: PingWebSocketFrame) -> Unit) {
    readPing1 = readMessage
  }

  override fun readPing(client: T, msg: PingWebSocketFrame) {
    readPing1?.also { it(client, msg) } ?: super.readPing(client, msg)
  }

  private var readPing2: ((client: T, msg: ByteBuf) -> Unit)? = null

  @JvmName("readPing2")
  fun readPing(readMessage: (client: T, msg: ByteBuf) -> Unit) {
    readPing2 = readMessage
  }

  override fun readPing(client: T, msg: ByteBuf) {
    readPing2?.also { it(client, msg) } ?: super.readPing(client, msg)
  }

  private var readPing3: ((client: T, msg: ByteBuffer) -> Unit)? = null

  @JvmName("readPing3")
  fun readPing(readMessage: (client: T, msg: ByteBuffer) -> Unit) {
    readPing3 = readMessage
  }

  override fun readPing(client: T, msg: ByteBuffer) {
    readPing3?.also { it(client, msg) } ?: super.readPing(client, msg)
  }

  private var readPing4: ((client: T, msg: ByteArray) -> Unit)? = null

  @JvmName("readPing4")
  fun readPing(readMessage: (client: T, msg: ByteArray) -> Unit) {
    readPing4 = readMessage
  }

  override fun readPing(client: T, msg: ByteArray) {
    readPing4?.also { it(client, msg) } ?: super.readPing(client, msg)
  }

  private var readPing5: ((client: T, msg: String) -> Unit)? = null

  @JvmName("readPing5")
  fun readPing(readMessage: (client: T, msg: String) -> Unit) {
    readPing5 = readMessage
  }

  override fun readPing(client: T, msg: String) {
    readPing5?.also { it(client, msg) } ?: super.readPing(client, msg)
  }

  private var readPong1: ((client: T, msg: PongWebSocketFrame) -> Unit)? = null

  @JvmName("readPong1")
  fun readPong(readMessage: (client: T, msg: PongWebSocketFrame) -> Unit) {
    readPong1 = readMessage
  }

  override fun readPong(client: T, msg: PongWebSocketFrame) {
    readPong1?.also { it(client, msg) } ?: super.readPong(client, msg)
  }

  private var readPong2: ((client: T, msg: ByteBuf) -> Unit)? = null

  @JvmName("readPong2")
  fun readPong(readMessage: (client: T, msg: ByteBuf) -> Unit) {
    readPong2 = readMessage
  }

  override fun readPong(client: T, msg: ByteBuf) {
    readPong2?.also { it(client, msg) } ?: super.readPong(client, msg)
  }

  private var readPong3: ((client: T, msg: ByteBuffer) -> Unit)? = null

  @JvmName("readPong3")
  fun readPong(readMessage: (client: T, msg: ByteBuffer) -> Unit) {
    readPong3 = readMessage
  }

  override fun readPong(client: T, msg: ByteBuffer) {
    readPong3?.also { it(client, msg) } ?: super.readPong(client, msg)
  }

  private var readPong4: ((client: T, msg: ByteArray) -> Unit)? = null

  @JvmName("readPong4")
  fun readPong(readMessage: (client: T, msg: ByteArray) -> Unit) {
    readPong4 = readMessage
  }

  override fun readPong(client: T, msg: ByteArray) {
    readPong4?.also { it(client, msg) } ?: super.readPong(client, msg)
  }

  private var readPong5: ((client: T, msg: String) -> Unit)? = null

  @JvmName("readPong5")
  fun readPong(readMessage: (client: T, msg: String) -> Unit) {
    readPong5 = readMessage
  }

  override fun readPong(client: T, msg: String) {
    readPong5?.also { it(client, msg) } ?: super.readPong(client, msg)
  }
}