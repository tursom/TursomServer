package cn.tursom.channel.enhance.impl

import cn.tursom.channel.enhance.ChannelWriter
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CoroutineContextChannelWriter<T>(
  var context: CoroutineContext,
  private val prevWriter: ChannelWriter<T>,
) : ChannelWriter<T> {
  override suspend fun write(vararg value: T) {
    withContext(context) {
      prevWriter.write(*value)
    }
  }

  override suspend fun write(value: Collection<T>) {
    withContext(context) {
      prevWriter.write(value)
    }
  }

  override suspend fun writeAndFlush(value: T, timeout: Long): Long {
    return withContext(context) {
      prevWriter.writeAndFlush(value, timeout)
    }
  }

  override suspend fun write(value: T) {
    withContext(context) {
      prevWriter.write(value)
    }
  }

  override suspend fun flush(timeout: Long): Long {
    return withContext(context) {
      prevWriter.flush()
    }
  }

  override fun close() {
    prevWriter.close()
  }
}

