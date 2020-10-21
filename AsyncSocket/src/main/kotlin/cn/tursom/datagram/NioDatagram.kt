package cn.tursom.datagram

import cn.tursom.niothread.NioThread
import java.net.SocketAddress
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey

open class NioDatagram(
  override val channel: DatagramChannel,
  override val key: SelectionKey,
  override val nioThread: NioThread
) : AsyncDatagram {
  override val open: Boolean get() = channel.isOpen && key.isValid
  override val remoteAddress: SocketAddress get() = channel.remoteAddress
  override fun writeMode() {}

  override fun close() {
    if (channel.isOpen || key.isValid) {
      nioThread.execute {
        channel.close()
        key.cancel()
      }
      nioThread.wakeup()
    }
  }

  override fun toString(): String {
    return "NioDatagram(channel=$channel, key=$key, nioThread=$nioThread)"
  }
}