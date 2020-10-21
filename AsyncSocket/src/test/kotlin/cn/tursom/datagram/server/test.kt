package cn.tursom.datagram.server

import cn.tursom.channel.AsyncChannel
import cn.tursom.channel.BufferedAsyncChannel
import cn.tursom.core.log
import cn.tursom.core.logE
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.datagram.AsyncDatagramClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val coroutineNumber = AtomicInteger(0)
private val sockets = ConcurrentHashMap<AsyncChannel, Unit>().keySet(Unit)
val echoHandler: suspend BufferedAsyncChannel.() -> Unit = {
  //System.err.println(coroutineNumber.incrementAndGet())
  sockets.add(this)
  try {
    while (open) {
      val buffer = read(60 * 1000)
      //log("$this recv from client $remoteAddress: ${buffer.toString(buffer.readable)}")
      sockets.forEach {
        //System.err.println(it)
        it.write(buffer.slice(buffer.readPosition, buffer.readable, writePosition = buffer.readable))
      }
    }
  } finally {
    sockets.remove(this)
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
    GlobalScope.launch {
      while (true) {
        val read = try {
          client.read(3000)
        } catch (e: Exception) {
          logE("socket closed")
          client.close()
          client = AsyncDatagramClient.connect("127.0.0.1", port).getBuffed(pool)
          //client.write(line)
          client.read(3000)
        }
        log("recv from server: ${read.getString()}")
        read.close()
      }
    }
    while (true) {
      try {
        print(">>>")
        val line = input.readLine()
        if (line.isEmpty()) continue
        client.write(line)
      } catch (e: Exception) {
        Exception(e).printStackTrace()
        client.close()
        client = AsyncDatagramClient.connect("127.0.0.1", port).getBuffed(pool)
      }
    }
  }
}