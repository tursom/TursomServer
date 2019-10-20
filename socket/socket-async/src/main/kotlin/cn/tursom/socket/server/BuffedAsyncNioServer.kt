package cn.tursom.socket.server

import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.core.bytebuffer.ByteArrayAdvanceByteBuffer
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.pool.usingAdvanceByteBuffer
import cn.tursom.socket.AsyncNioSocket

class BuffedAsyncNioServer(
    port: Int,
    backlog: Int = 50,
    memoryPool: MemoryPool,
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
  ) : this(port, backlog, DirectMemoryPool(blockSize, blockCount), handler)
}