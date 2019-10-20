package cn.tursom.socket.server

import cn.tursom.socket.BaseSocket
import org.junit.Test

class SingleThreadSocketServerTest {
  @Test
  fun testCreateServer() {
    val port = 12345

    // Kotlin 写法
    SingleThreadSocketServer(port) {
    }.close()

    // Java 写法
    SingleThreadSocketServer(port, object : ISimpleSocketServer.Handler {
      override fun handle(socket: BaseSocket) {
      }
    }).close()
  }
}