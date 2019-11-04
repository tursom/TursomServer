package cn.tursom.socket.server

import cn.tursom.core.bytebuffer.ByteArrayAdvanceByteBuffer
import cn.tursom.core.log
import cn.tursom.core.logE
import cn.tursom.socket.AsyncAioClient
import cn.tursom.socket.SocketClient
import kotlinx.coroutines.runBlocking
import org.junit.Test

class AsyncNioServerTest {
  private val testMsg = "hello"
  private val port = 12345
  private val server = AsyncNioServer(port) {
    log("new connection")
    val buffer = ByteArrayAdvanceByteBuffer(1024)
    while (true) {
      buffer.clear()
      read(buffer, 5000)
      logE("server recv: ${buffer.toString(buffer.readableSize)}")
      write(buffer, 5000)
    }
  }
  
  init {
    server.run()
  }
  
  //@Test
  fun testAsyncNioServer() {
    runBlocking {
      val client = AsyncAioClient.connect("127.0.0.1", port)
      log("connect to server")
      val buffer = ByteArrayAdvanceByteBuffer(1024)
      repeat(10) {
        buffer.clear()
        buffer.put(testMsg)
        client.write(buffer, 5000)
        buffer.clear()
        client.read(buffer, 5000)
        log("server recv: ${buffer.getString()}")
      }
    }
  }
  
  
  //@Test
  fun testAsyncNioServerSocket() {
    SocketClient("localhost", port) {
      val buffer = ByteArray(1024)
      repeat(10) {
        send(testMsg)
        val readSize = inputStream.read(buffer)
        val recv = String(buffer, 0, readSize)
        log(recv)
      }
    }
  }
}