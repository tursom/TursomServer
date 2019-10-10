import cn.tursom.core.bytebuffer.ByteArrayAdvanceByteBuffer
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.core.pool.usingAdvanceByteBuffer
import cn.tursom.socket.server.AsyncNioServer

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
            // 从套接字中读数据，五秒之内没有数据就抛出异常
            read(buffer, 5000)
            // 输出读取到的数据
            println("${System.currentTimeMillis()}: recv from ${channel.remoteAddress}: ${buffer.toString(buffer.readableSize)}")
            // 原封不动的返回数据
            write(buffer)
            // 代码块结束后，框架会自动释放连接
        }
    }
    // 创建一个新线程去启动服务器
    Thread(server, "echoServerStarter").start()
}