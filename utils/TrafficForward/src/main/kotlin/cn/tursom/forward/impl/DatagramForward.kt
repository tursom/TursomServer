package cn.tursom.forward.impl

import cn.tursom.forward.Forward
import java.net.InetSocketAddress
import java.net.SocketAddress

class DatagramForward(
  host: SocketAddress,
  timeout: Long = 3,
  forward: Forward? = null
) : NIOForward(host, udp(), timeout, forward) {
  constructor(
    host: String,
    port: Int,
    timeout: Long = 3,
    forward: Forward? = null
  ) : this(InetSocketAddress(host, port), timeout, forward)
}