package cn.tursom.channel.enhance

import cn.tursom.core.ShutdownHook
import cn.tursom.core.pool.HeapMemoryPool
import cn.tursom.core.pool.MemoryPool
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

@OptIn(DelicateCoroutinesApi::class)
class ChannelPipeline<V>(
  context: CoroutineContext,
  private val reader: ChannelReader<V>,
  private val writer: ChannelWriter<V>,
  private val pool: MemoryPool = HeapMemoryPool(),
) : Closeable {
  private val job = GlobalScope.launch(context) {
    while (true) {
      val buffer = reader.read(pool)
      writer.write(buffer)
    }
  }

  @Suppress("unused")
  private val hook = ShutdownHook.addHook(softReference = true) {
    close()
  }

  override fun close() {
    job.cancel()
  }
}
