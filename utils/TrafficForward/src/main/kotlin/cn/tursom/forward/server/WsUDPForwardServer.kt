package cn.tursom.forward.server

import cn.tursom.forward.impl.NIOForward
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.channels.DatagramChannel

/**
 * 预组装流量转发服务器
 * 监听wsPort端口，并讲所有流量以UDP的方式转发到udpHost上
 * 每个WS连接分别对应一个UDP连接
 */
class WsUDPForwardServer(
  var udpHost: SocketAddress,
  wsPort: Int,
  readTimeout: Int? = 60,
  writeTimeout: Int? = 60,
  webSocketPath: String = "/ws",
  bodySize: Int = 512 * 1024
) : WsForwardServer(
  wsPort,
  readTimeout,
  writeTimeout,
  webSocketPath,
  bodySize,
  { NIOForward(udpHost, DatagramChannel.open()) }
) {
  constructor(
    udpHost: String,
    udpPort: Int,
    wsPort: Int,
    readTimeout: Int? = 60,
    writeTimeout: Int? = 60,
    webSocketPath: String = "/ws",
    bodySize: Int = 512 * 1024
  ) : this(InetSocketAddress(udpHost, udpPort), wsPort, readTimeout, writeTimeout, webSocketPath, bodySize)

  fun setUdpHost(host: String, port: Int) {
    udpHost = InetSocketAddress(host, port)
  }
}

