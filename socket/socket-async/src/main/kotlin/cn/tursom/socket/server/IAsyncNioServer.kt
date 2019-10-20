package cn.tursom.socket.server

import cn.tursom.socket.AsyncNioSocket

interface IAsyncNioServer : ISocketServer {
  val handler: suspend AsyncNioSocket.() -> Unit
}