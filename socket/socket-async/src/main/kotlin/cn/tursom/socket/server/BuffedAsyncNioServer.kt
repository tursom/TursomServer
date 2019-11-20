package cn.tursom.socket.server

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.core.pool.MemoryPool
import cn.tursom.socket.AsyncNioSocket

/**
 * 带内存池的 NIO 套接字服务器。<br />
 * 其构造函数是标准写法的改造，会向 handler 方法传入一个 ByteBuffer，默认是 DirectByteBuffer，
 * 当内存池用完之后会换为 ByteArrayByteBuffer。
 */
class BuffedAsyncNioServer(
    port: Int,
    memoryPool: MemoryPool,
    backlog: Int = 50,
    handler: suspend AsyncNioSocket.(buffer: ByteBuffer) -> Unit
) : IAsyncNioServer by AsyncNioServer(port, backlog, {
  memoryPool {
    handler(it)
  }
}) {
  constructor(
      port: Int,
      blockSize: Int = 1024,
      blockCount: Int = 128,
      backlog: Int = 50,
      handler: suspend AsyncNioSocket.(buffer: ByteBuffer) -> Unit
  ) : this(port, DirectMemoryPool(blockSize, blockCount), backlog, handler)
}