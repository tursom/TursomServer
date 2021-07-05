package cn.tursom.core.ws

import io.netty.channel.socket.SocketChannel
import java.net.URI

/**
 * usually use SimpWebSocketHandler
 * @see SimpWebSocketHandler
 */
class SimpWebSocketClient<H : WebSocketHandler<SimpWebSocketClient<H>, H>>(
  url: String,
  handler: H,
  autoWrap: Boolean = true,
  log: Boolean = false,
  compressed: Boolean = true,
  maxContextLength: Int = 4096,
  headers: Map<String, String>? = null,
  handshakerUri: URI? = null,
  autoRelease: Boolean = true,
  initChannel: ((ch: SocketChannel) -> Unit)? = null,
) : WebSocketClient<SimpWebSocketClient<H>, H>(
  url,
  handler,
  autoWrap,
  log,
  compressed,
  maxContextLength,
  headers,
  handshakerUri,
  autoRelease,
  initChannel
)
