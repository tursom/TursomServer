package cn.tursom.datagram.server

import cn.tursom.channel.BufferedAsyncChannel
import cn.tursom.core.ByteBufferUtil
import cn.tursom.core.buffer.impl.ArrayByteBuffer
import cn.tursom.core.coroutine.GlobalScope
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.core.util.ThreadLocalSimpleDateFormat
import cn.tursom.datagram.AsyncDatagramClient
import cn.tursom.log.impl.Slf4jImpl
import cn.tursom.socket.NioClient
import cn.tursom.socket.server.BufferedNioServer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val logger = Slf4jImpl.getLogger()

val echoHandler: suspend BufferedAsyncChannel.() -> Unit = {
  while (true) {
    val buffer = read()
    logger.debug("$this recv from client $remoteAddress: ${buffer.toString(buffer.readable)}")
    write(buffer)
  }
}

fun main() {
  GlobalScope.launch {}
  val port = 12345
  val pool = DirectMemoryPool(1024, 16)
  val server = BufferedNioServer(port, pool, handler = echoHandler)
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
    var client = NioClient.connect("127.0.0.1", port).getBuffed(pool)
    while (true) {
      try {
        print(">>>")
        val line = input.readLine()
        if (line.isEmpty()) continue
        client.write(ArrayByteBuffer(
          ByteBufferUtil.wrap("["),
          ByteBufferUtil.wrap(ThreadLocalSimpleDateFormat.cn.now()),
          ByteBufferUtil.wrap("] send from client: "),
          ByteBufferUtil.wrap(line),
        ))
        val read = try {
          client.read(3000)
        } catch (e: Exception) {
          client.close()
          client = AsyncDatagramClient.connect("127.0.0.1", port).getBuffed(pool)
          client.write(line)
          client.read(3000)
        }
        logger.debug("recv from server: ${read.getString()}")
        read.close()
      } catch (e: Exception) {
        Exception(e).printStackTrace()
        client.close()
        client = AsyncDatagramClient.connect("127.0.0.1", port).getBuffed(pool)
      }
    }
  }
}