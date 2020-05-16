package cn.tursom.datagram

import cn.tursom.niothread.NioThread
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey

class NioDatagram(
  override val channel: DatagramChannel,
  override val key: SelectionKey,
  override val nioThread: NioThread
) : AsyncDatagram {
  override val open: Boolean get() = channel.isOpen && key.isValid

  private inline fun <T> operate(action: () -> T): T {
    return try {
      action()
    } catch (e: Exception) {
      waitMode()
      throw e
    }
  }

  override suspend fun <T> write(timeout: Long, action: () -> T): T {
    return operate {
      waitWrite(timeout)
      action()
    }
  }

  override suspend fun <T> read(timeout: Long, action: () -> T): T {
    return operate {
      waitRead(timeout)
      action()
    }
  }

  override fun close() {
    if (channel.isOpen || key.isValid) {
      nioThread.execute {
        channel.close()
        key.cancel()
      }
      nioThread.wakeup()
    }
  }
}