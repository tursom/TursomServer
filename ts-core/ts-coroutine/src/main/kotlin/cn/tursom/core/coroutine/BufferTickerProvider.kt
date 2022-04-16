package cn.tursom.core.coroutine

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.Reference
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentLinkedQueue

class BufferTickerProvider(
  val delayMillis: Long,
  val capacity: Int = 16,
) {
  private val channels = ConcurrentLinkedQueue<Reference<out SendChannel<Unit>>>()

  init {
    @OptIn(DelicateCoroutinesApi::class)
    GlobalScope.launch {
      var time = System.currentTimeMillis()
      @Suppress("ControlFlowWithEmptyBody")
      while (true) {
        val iterator = channels.iterator()
        iterator.forEach { reference ->
          val channel = reference.get()
          if (channel == null) {
            iterator.remove()
            return@forEach
          }
          if (channel.trySend(Unit).isClosed) {
            channel.close()
            iterator.remove()
          }
        }
        delay(delayMillis - (System.currentTimeMillis() - time))
        time = System.currentTimeMillis()
      }
    }
  }

  fun get(capacity: Int = this.capacity): ReceiveChannel<Unit> {
    val channel = Channel<Unit>(capacity)
    channels.add(WeakReference(channel))
    return channel
  }
}