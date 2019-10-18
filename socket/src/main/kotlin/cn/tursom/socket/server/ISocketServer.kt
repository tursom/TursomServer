package cn.tursom.socket.server

import java.io.Closeable

interface ISocketServer : Runnable, Closeable {
  val port: Int
}