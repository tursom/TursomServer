package cn.tursom.forward.server

import cn.tursom.forward.Forward
import cn.tursom.forward.impl.NettyForward
import cn.tursom.forward.socket.SocketServer

open class SocketForwardServer(
  port: Int,
  readTimeout: Int? = 60,
  writeTimeout: Int? = 60,
  forward: () -> Forward
) : SocketServer(
  port, { NettyForward(it, forward()) }, readTimeout, writeTimeout
)