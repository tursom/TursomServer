package cn.tursom.socket.server

import cn.tursom.socket.BaseSocket

interface ISimpleSocketServer : ISocketServer {
  val handler: BaseSocket.() -> Unit

  interface Handler {
    fun handle(socket: BaseSocket)
  }
}