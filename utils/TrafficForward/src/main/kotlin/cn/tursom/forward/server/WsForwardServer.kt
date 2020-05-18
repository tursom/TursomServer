package cn.tursom.forward.server

import cn.tursom.forward.Forward
import cn.tursom.forward.impl.WSForward
import cn.tursom.forward.ws.WebSocketServer

open class WsForwardServer(
  port: Int,
  readTimeout: Int? = 60,
  writeTimeout: Int? = 60,
  webSocketPath: String = "/ws",
  bodySize: Int = 512 * 1024,
  forward: () -> Forward
) : WebSocketServer(
  port, { WSForward(it, forward()) }, readTimeout, writeTimeout, webSocketPath, bodySize
)