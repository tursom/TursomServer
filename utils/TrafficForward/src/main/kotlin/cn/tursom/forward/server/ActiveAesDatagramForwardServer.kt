package cn.tursom.forward.server

import cn.tursom.core.encrypt.PublicKeyEncrypt
import cn.tursom.core.encrypt.RSA
import cn.tursom.forward.Forward
import cn.tursom.forward.datagram.DatagramServer
import cn.tursom.forward.impl.ActiveAesNettyForward
import cn.tursom.forward.impl.NettyForward

open class ActiveAesDatagramForwardServer(
  port: Int,
  readTimeout: Int? = 60,
  writeTimeout: Int? = 60,
  private val aesBuilder: () -> PublicKeyEncrypt = { RSA() },
  forward: () -> Forward
) : DatagramServer(
  port, { ActiveAesNettyForward(it, forward(), aesBuilder()) }, readTimeout, writeTimeout
)