import cn.tursom.core.bytebuffer.ByteArrayAdvanceByteBuffer
import cn.tursom.core.log
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.core.pool.usingAdvanceByteBuffer
import cn.tursom.socket.AsyncNioClient
import cn.tursom.socket.server.AsyncNioServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.net.SocketException
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger

fun main() {
  // 服务器端口，可任意指定
  val port = 12345
  
  // 创建一个直接内存池，每个块是1024字节，共有256个快
  val memoryPool = DirectMemoryPool(1024, 256)
  // 创建服务器对象
  val server = AsyncNioServer(port) {
    // 这里处理业务逻辑，套接字对象被以 this 的方式传进来
    // 从内存池中获取一个内存块
    memoryPool.usingAdvanceByteBuffer {
      // 检查是否获取成功，不成功就创建一个堆缓冲
      val buffer = it ?: ByteArrayAdvanceByteBuffer(1024)
      try {
        while (true) {
          buffer.clear()
          // 从套接字中读数据，五秒之内没有数据就抛出异常
          if (read(buffer, 10_000) < 0) {
            return@AsyncNioServer
          }
          // 输出读取到的数据
          //log("server recv from ${channel.remoteAddress}: [${buffer.readableSize}] ${buffer.toString(buffer.readableSize)}")
          // 原封不动的返回数据
          val writeSize = write(buffer)
          //log("server send [$writeSize] bytes")
        }
      } catch (e: TimeoutException) {
      }
      // 代码块结束后，框架会自动释放连接
    }
  }
  server.run()
  
  val connectionCount = 300
  val dataPerConn = 10
  val testData = "testData".toByteArray()
  
  val remain = AtomicInteger(connectionCount)
  
  val clientMemoryPool = DirectMemoryPool(1024, connectionCount)
  
  val start = System.currentTimeMillis()
  
  repeat(connectionCount) {
    GlobalScope.launch {
      val socket = AsyncNioClient.connect("127.0.0.1", port)
      clientMemoryPool.usingAdvanceByteBuffer {
        // 检查是否获取成功，不成功就创建一个堆缓冲
        val buffer = it ?: ByteArrayAdvanceByteBuffer(1024)
        try {
          repeat(dataPerConn) {
            buffer.clear()
            buffer.put(testData)
            //log("client sending: [${buffer.readableSize}] ${buffer.toString(buffer.readableSize)}")
            val writeSize = socket.write(buffer)
            //log("client write [$writeSize] bytes")
            //log(buffer.toString())
            val readSize = socket.read(buffer)
            //log(buffer.toString())
            //log("client recv: [$readSize:${buffer.readableSize}] ${buffer.toString(buffer.readableSize)}")
          }
        } catch (e: Exception) {
          Exception(e).printStackTrace()
        } finally {
          socket.close()
        }
      }
      remain.decrementAndGet()
    }
  }
  
  while (remain.get() != 0) {
    println(remain.get())
    sleep(500)
  }
  
  val end = System.currentTimeMillis()
  println(end - start)
  server.close()
}