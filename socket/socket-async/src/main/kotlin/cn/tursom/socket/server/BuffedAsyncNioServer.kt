package cn.tursom.socket.server

import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.core.bytebuffer.ByteArrayAdvanceByteBuffer
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.pool.usingAdvanceByteBuffer
import cn.tursom.socket.AsyncNioSocket

/**
 * 带内存池的 NIO 套接字服务器。<br />
 * 其构造函数是标准写法的改造，会向 handler 方法传入一个 AdvanceByteBuffer，默认是 DirectAdvanceByteBuffer，
 * 当内存池用完之后会换为 ByteArrayAdvanceByteBuffer。
 */
class BuffedAsyncNioServer(
    port: Int,
    memoryPool: MemoryPool,
    backlog: Int = 50,
    handler: suspend AsyncNioSocket.(buffer: AdvanceByteBuffer) -> Unit
) : IAsyncNioServer by AsyncNioServer(port, backlog, {
  memoryPool.usingAdvanceByteBuffer {
    handler(it ?: ByteArrayAdvanceByteBuffer(memoryPool.blockSize))
  }
}) {
  constructor(
      port: Int,
      blockSize: Int = 1024,
      blockCount: Int = 128,
      backlog: Int = 50,
      handler: suspend AsyncNioSocket.(buffer: AdvanceByteBuffer) -> Unit
  ) : this(port, DirectMemoryPool(blockSize, blockCount), backlog, handler)
}