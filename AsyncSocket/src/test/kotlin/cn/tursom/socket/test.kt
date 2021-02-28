package cn.tursom.socket

import cn.tursom.core.pool.HeapMemoryPool
import cn.tursom.socket.server.BufferedNioServer
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

private val sockets = ConcurrentHashMap<AsyncSocket, Unit>().keySet(Unit)
val handler: suspend BufferedAsyncSocket.() -> Unit = {
  sockets.add(this)
  try {
    while (open) {
      val read = read(60 * 1000)
      println(read.toString(read.readable))
      sockets.forEach {
        System.err.println(it)
        it.write(read)
      }
    }
  } finally {
    sockets.remove(this)
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