package cn.tursom.socket.server

import cn.tursom.socket.BaseSocket

interface SocketServer : ISocketServer {
  val handler: BaseSocket.() -> Unit

  companion object {
    val cpuNumber = Runtime.getRuntime().availableProcessors() //CPU处理器的个数
  }
}