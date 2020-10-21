package cn.tursom.socket

import cn.tursom.core.pool.HeapMemoryPool
import cn.tursom.socket.server.BufferedNioServer
import kotlinx.coroutines.runBlocking

val handler: suspend BufferedAsyncSocket.() -> Unit = {
  while (open) {
    val read = read()
    println(read.toString(read.readable))
    write(read)
  }
}

fun main() {
  val server = BufferedNioServer(12345, handler = handler)
  server.run()
  runBlocking {
    BufferedNioSocket(NioSocket("localhost", 12345), server.memoryPool).use { socket ->
      socket.write("hello")
      println(socket.read().getString())
    }
  }
}