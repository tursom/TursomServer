package cn.tursom.datagram

import cn.tursom.channel.AsyncNioChannel
import cn.tursom.channel.read
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.pool.MemoryPool
import java.net.SocketException
import java.nio.channels.DatagramChannel

interface AsyncDatagram : AsyncNioChannel {
  override val channel: DatagramChannel
  override fun getBuffed(pool: MemoryPool): BufferedAsyncDatagram = BufferedNioDatagram(pool, this)

  /**
   * 在有数据读取的时候自动由内存池分配内存
   */
  override suspend fun read(pool: MemoryPool, timeout: Long): ByteBuffer = read(timeout) {
    val buffer = pool.get()
    if (channel.read(buffer) < 0) throw SocketException()
    buffer
  }
}