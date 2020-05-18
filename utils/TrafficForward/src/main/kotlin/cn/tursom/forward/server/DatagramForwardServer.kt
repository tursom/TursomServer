package cn.tursom.forward.server

import cn.tursom.forward.Forward
import cn.tursom.forward.datagram.DatagramServer
import cn.tursom.forward.impl.NettyForward

open class DatagramForwardServer(
  port: Int,
  readTimeout: Int? = 60,
  writeTimeout: Int? = 60,
  forward: () -> Forward
) : DatagramServer(
  port, { NettyForward(it, forward()) }, readTimeout, writeTimeout
)