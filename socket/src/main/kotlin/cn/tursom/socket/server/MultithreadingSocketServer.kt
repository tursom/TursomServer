package cn.tursom.socket.server

import cn.tursom.core.cpuNumber
import cn.tursom.socket.BaseSocket
import java.net.ServerSocket

/**
 * 这是一个自动启用多个线程来处理请求的套接字服务器
 */
class MultithreadingSocketServer(
    private val serverSocket: ServerSocket,
    private val threadNumber: Int = cpuNumber,
    override val handler: BaseSocket.() -> Unit
) : ISimpleSocketServer {
  override val port = serverSocket.localPort

  constructor(
      port: Int,
      threadNumber: Int = cpuNumber,
      handler: BaseSocket.() -> Unit
  ) : this(ServerSocket(port), threadNumber, handler)

  constructor(
      port: Int,
      handler: BaseSocket.() -> Unit
  ) : this(port, cpuNumber, handler)

  constructor(
      port: Int,
      threadNumber: Int = cpuNumber,
      handler: ISimpleSocketServer.Handler
  ) : this(ServerSocket(port), threadNumber, handler::handle)

  constructor(
      port: Int,
      handler: ISimpleSocketServer.Handler
  ) : this(port, cpuNumber, handler::handle)

  private val threadList = ArrayList<Thread>()

  override fun run() {
    for (i in 1..threadNumber) {
      val thread = Thread {
        while (true) {
          serverSocket.accept().use {
            try {
              BaseSocket(it).handler()
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }
        }
      }
      thread.start()
      threadList.add(thread)
    }
  }

  override fun close() {
    serverSocket.close()
  }
}