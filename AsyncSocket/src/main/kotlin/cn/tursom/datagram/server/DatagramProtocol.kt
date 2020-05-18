package cn.tursom.datagram.server

import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.buffer.write
import cn.tursom.niothread.NioProtocol
import cn.tursom.niothread.NioThread
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import kotlin.coroutines.resume

object DatagramProtocol : NioProtocol {
  override fun handleRead(key: SelectionKey, nioThread: NioThread) {
    val attr = key.attachment() as AsyncDatagramServer
    val datagramChannel = key.channel() as DatagramChannel
    val buffer = HeapByteBuffer(1024)
    val address = buffer.write { datagramChannel.receive(it) }
    val channel = attr.getChannel(address)
    channel.addBuffer(buffer)
    channel.cont?.resume(0)
  }

  override fun handleWrite(key: SelectionKey, nioThread: NioThread) {
  }
}