package cn.tursom.channel.enhance

import cn.tursom.core.coroutine.GlobalScope
import cn.tursom.core.pool.HeapMemoryPool
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.util.ShutdownHook
import kotlinx.coroutines.DelicateCoroutinesApi
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
  private val hook = ShutdownHook.addSoftShutdownHook {
    close()
  }

  override fun close() {
    job.cancel()
  }
}
