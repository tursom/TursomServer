package cn.tursom.datagram.server

import cn.tursom.channel.BufferedAsyncChannel
import cn.tursom.core.log
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.datagram.AsyncDatagramClient
import cn.tursom.socket.NioClient
import cn.tursom.socket.NioSocket
import cn.tursom.socket.server.BufferedNioServer
import kotlinx.coroutines.runBlocking

val echoHandler: suspend BufferedAsyncChannel.() -> Unit = {
  while (true) {
    val buffer = read()
    log("$this recv from client $remoteAddress: ${buffer.toString(buffer.readable)}")
    //Throwable().printStackTrace()
    write(buffer)
  }
}

fun main() {
  val port = 12345
  val pool = DirectMemoryPool(1024, 16)
  val server = BufferedAsyncDatagramServer(port, pool, handler = echoHandler)
  //val server = LoopDatagramServer(port, protocol = object : NioProtocol {
  //  override fun handleRead(key: SelectionKey, nioThread: NioThread) {
  //    val datagramChannel = key.channel() as DatagramChannel
  //    val buffer = HeapByteBuffer(1024)
  //    val address = buffer.write { datagramChannel.receive(it) }
  //    log("recv from client $address: ${buffer.toString(buffer.readable)}")
  //    buffer.read { datagramChannel.send(it, address) }
  //  }
  //
  //  override fun handleWrite(key: SelectionKey, nioThread: NioThread) {
  //  }
  //})
  server.run()

  runBlocking {
    val input = System.`in`.bufferedReader()
    var client = AsyncDatagramClient.connect("127.0.0.1", port).getBuffed(pool)
    while (true) {
      try {
        print(">>>")
        val line = input.readLine()
        if (line.isEmpty()) continue
        client.write(line)
        val read = try {
          client.read(3000)
        } catch (e: Exception) {
          client.close()
          client = AsyncDatagramClient.connect("127.0.0.1", port).getBuffed(pool)
          client.write(line)
          client.read(3000)
        }
        log("recv from server: ${read.getString()}")
        read.close()
      } catch (e: Exception) {
        Exception(e).printStackTrace()
        client.close()
        client = AsyncDatagramClient.connect("127.0.0.1", port).getBuffed(pool)
      }
    }
  }
}