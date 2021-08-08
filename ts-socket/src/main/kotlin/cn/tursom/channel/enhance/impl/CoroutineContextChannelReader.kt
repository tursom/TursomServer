package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelReader
import cn.tursom.core.pool.MemoryPool
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CoroutineContextChannelReader<T>(
  var context: CoroutineContext,
  private val prevReader: ChannelReader<T>,
) : ChannelReader<T> {
  override suspend fun read(pool: MemoryPool, timeout: Long): T {
    return withContext(context) {
      prevReader.read(pool, timeout)
    }
  }

  override fun close() {
    prevReader.close()
  }
}
