package cn.tursom.web.client.netty

import cn.tursom.core.util.seconds
import io.netty.handler.timeout.WriteTimeoutHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class HttpConnectionPool(
  host: String,
  port: Int,
  ssl: Boolean,
  private val maxConn: Int = 5,
) {
  companion object {
    private data class PoolDesc(val host: String, val port: Int, val ssl: Boolean)

    private val poolCache = ConcurrentHashMap<PoolDesc, HttpConnectionPool>()
    fun poolOf(host: String, port: Int, ssl: Boolean) = poolCache.getOrPut(PoolDesc(host, port, ssl)) {
      HttpConnectionPool(host, port, ssl)
    }
  }

  private val group = HttpExecutor.group(host, port, ssl) {
    it.pipeline()
      .addLast(NettyHttpResultResume)
      .addLast(WriteTimeoutHandler(60))
    it.attr(NettyHttpResultResume.countKey).set(conn)
  }
  private val pool = Channel<NettyHttpConnection>(maxConn)
  private val conn = AtomicInteger()

  suspend fun <R> useConnection(handler: suspend (NettyHttpConnection) -> R): R {
    val client = if (conn.getAndIncrement() < maxConn) {
      group()
    } else {
      conn.decrementAndGet()
      pool.receive()
    }
    delay(10.seconds().toMillis())
    val result = try {
      handler(client)
    } catch (e: Exception) {
      client.channel.close()
      throw e
    }
    pool.send(client)
    return result
  }
}
