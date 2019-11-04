package cn.tursom.socket.server

import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.core.bytebuffer.ByteArrayAdvanceByteBuffer
import cn.tursom.core.bytebuffer.readNioBuffer
import cn.tursom.core.bytebuffer.writeNioBuffer
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.core.pool.MemoryPool
import cn.tursom.socket.INioProtocol
import cn.tursom.socket.SocketClient
import cn.tursom.socket.niothread.INioThread
import org.junit.Test
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel

class NioServerTest {
  private val port = 12345
  @Test
  fun testNioServer() {
    val memoryPool: MemoryPool = DirectMemoryPool(1024, 256)
    val server = NioServer(port, object : INioProtocol {
      override fun handleConnect(key: SelectionKey, nioThread: INioThread) {
        val memoryToken = memoryPool.allocate()
        key.attach(memoryToken to (memoryPool.getAdvanceByteBuffer(memoryToken) ?: ByteArrayAdvanceByteBuffer(1024)))
        key.interestOps(SelectionKey.OP_READ)
      }
      
      override fun handleRead(key: SelectionKey, nioThread: INioThread) {
        val channel = key.channel() as SocketChannel
        val buffer = (key.attachment() as Pair<Int, AdvanceByteBuffer>).second
        buffer.writeNioBuffer {
          channel.read(it)
        }
        println("record from client: ${buffer.toString(buffer.readableSize)}")
        key.interestOps(SelectionKey.OP_WRITE)
      }
      
      override fun handleWrite(key: SelectionKey, nioThread: INioThread) {
        val channel = key.channel() as SocketChannel
        val buffer = (key.attachment() as Pair<Int, AdvanceByteBuffer>).second
        println("send to client: ${buffer.toString(buffer.readableSize)}")
        buffer.readNioBuffer {
          channel.write(it)
        }
        buffer.reset()
        key.interestOps(SelectionKey.OP_READ)
      }
      
      override fun exceptionCause(key: SelectionKey, nioThread: INioThread, e: Throwable) {
        super.exceptionCause(key, nioThread, e)
        val memoryToken = (key.attachment() as Pair<Int, AdvanceByteBuffer>).first
        memoryPool.free(memoryToken)
        key.channel().close()
        key.cancel()
      }
    })
    
    server.run()
    val socket = SocketClient("127.0.0.1", port)
    val buffer = ByteArray(1024)
    socket.outputStream.write("Hello".toByteArray())
    val readCount = socket.inputStream.read(buffer)
    println(buffer.copyOfRange(0, readCount).toString(Charsets.UTF_8))
    server.close()
  }
}