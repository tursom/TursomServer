###异步套接字的协程封装
这个包实现了对异步的套接字的语句同步化封装，适用于 Kotlin 协程执行环境。
但是因为需要协程作为执行环境，所以无法在 Java 环境下正常创建。
其核心分别是对 AIO 进行封装的 AsyncAioSocket 和对 NIO 进行封装的
AsyncNioSocket。AsyncAioSocket 实现简单，但是可塑性较低，缺陷也较难解决；
AsyncNioSocket 虽然实现复杂，但是可塑性很高，优化空间大，缺陷一般也都可以解决。
---
AsyncAioSocket 和 AsyncNioSocket 分别通过对应的服务器与客户端创建。
创建一个异步服务器的形式和同步服务器的形式是完全一样的：

```kotlin
// 创建一个自带内存池的异步套接字服务器
val server = BufferedAsyncNioServer(port) { buffer->
  // do any thing
  // 这里都是用同步语法写出的异步套接字操作
  read(buffer)
  write(buffer)
}
// 异步服务器不需要创建新线程来执行
server.run()

// 异步套接字的创建既可以在普通环境下，也可以在协程环境下
val client = AsyncNioClient.connect("localhost", port)
runBlocking {
  val buffer = ByteArrayAdvanceByteBuffer(1024)
  // 向套接字内写数据
  buffer.put("Hello!")
  client.write(buffer)
  // 从套接字内读数据
  buffer.reset()
  client.read(buffer)
  log(buffer.getString())
  client.close()
}
```