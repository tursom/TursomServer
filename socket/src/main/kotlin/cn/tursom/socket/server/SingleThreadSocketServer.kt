package cn.tursom.socket.server

import cn.tursom.socket.BaseSocket
import java.net.ServerSocket
import java.net.SocketException

/**
 * 单线程套接字服务器
 * 可以用多个线程同时运行该服务器，可以正常工作
 */
class SingleThreadSocketServer(
    private val serverSocket: ServerSocket,
    override val handler: BaseSocket.() -> Unit
) : ISimpleSocketServer {
  override val port = serverSocket.localPort

  constructor(
      port: Int,
      handler: BaseSocket.() -> Unit
  ) : this(ServerSocket(port), handler)

  constructor(
      port: Int,
      handler: ISimpleSocketServer.Handler
  ) : this(ServerSocket(port), handler::handle)

  override fun run() {
    while (!serverSocket.isClosed) {
      try {
        serverSocket.accept().use {
          try {
            BaseSocket(it).handler()
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      } catch (e: SocketException) {
        if (e.message == "Socket closed" || e.message == "cn.tursom.socket closed") {
          break
        } else {
          e.printStackTrace()
        }
      }
    }
  }

  override fun close() {
    try {
      serverSocket.close()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}