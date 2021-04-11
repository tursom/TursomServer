package cn.tursom.socket.server

import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.core.pool.ExpandableMemoryPool
import cn.tursom.core.pool.MarkedMemoryPool
import cn.tursom.core.pool.MemoryPool
import cn.tursom.socket.BufferedAsyncSocket
import cn.tursom.socket.BufferedNioSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

/**
 * 带内存池的 NIO 套接字服务器。
 * 在处理结束后会自动释放由内存池分配的内存
 */
open class BufferedNioServer(
  port: Int,
  val memoryPool: MemoryPool,
  backlog: Int = 50,
  coroutineScope: CoroutineScope = GlobalScope,
  autoCloseSocket: Boolean = true,
  handler: suspend BufferedAsyncSocket.() -> Unit
) : NioServer(port, backlog, coroutineScope, autoCloseSocket, {
  MarkedMemoryPool(memoryPool).use { marked ->
    BufferedNioSocket(this, marked).handler()
  }
}) {
  constructor(
    port: Int,
    blockSize: Int = 1024,
    blockCount: Int = 128,
    backlog: Int = 50,
    coroutineScope: CoroutineScope = GlobalScope,
    autoCloseSocket: Boolean = true,
    handler: suspend BufferedAsyncSocket.() -> Unit
  ) : this(
    port,
    ExpandableMemoryPool { DirectMemoryPool(blockSize, blockCount) },
    backlog,
    coroutineScope,
    autoCloseSocket,
    handler
  )

  override fun close() {
    super.close()
    memoryPool.gc()
  }
}