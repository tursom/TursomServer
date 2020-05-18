package cn.tursom.forward.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.forward.Forward
import cn.tursom.ws.WebSocketClient
import cn.tursom.ws.WebSocketHandler
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.util.internal.logging.InternalLogger
import io.netty.util.internal.logging.Slf4JLoggerFactory

class WsClientForward(uri: String, override var forward: Forward? = null) : Forward, WebSocketHandler {
  companion object {
    private val log: InternalLogger = Slf4JLoggerFactory.getInstance(WsClientForward::class.java)
  }

  val wsClient = WebSocketClient(uri, this)

  init {
    forward?.forward = this
  }

  override fun readMessage(client: WebSocketClient, msg: TextWebSocketFrame) {
    readMessage(client, msg.content())
  }

  override fun readMessage(client: WebSocketClient, msg: ByteBuffer) {
    forward?.write(msg) ?: msg.close()
  }

  override fun write(buffer: ByteBuffer) {
    val future = wsClient.writeText(buffer)
    future.addListener {
      buffer.close()
    } ?: buffer.close()
  }

  override fun onClose(client: WebSocketClient) {
    forward?.close()
  }

  override fun onError(client: WebSocketClient, e: Throwable) {
    log.error("exception caused on web socket client", e)
    close()
  }

  override fun close() {
    wsClient.close()
  }
}