package cn.tursom.forward.datagram

import io.netty.buffer.ByteBufAllocator
import io.netty.channel.*
import io.netty.channel.socket.ServerSocketChannelConfig
import java.net.SocketException
import java.nio.channels.DatagramChannel

class ServerDatagramChannelConfig(
  channel: Channel,
  private val datagramChannel: DatagramChannel
) : DefaultChannelConfig(channel), ServerSocketChannelConfig {
  init {
    setRecvByteBufAllocator(FixedRecvByteBufAllocator(2048))
  }

  private inline fun withThis(action: () -> Unit): ServerDatagramChannelConfig {
    action()
    return this
  }

  @Suppress("DEPRECATION")
  @Deprecated("super deprecated")
  override fun setMaxMessagesPerRead(n: Int) = withThis { super.setMaxMessagesPerRead(n) }
  override fun getBacklog(): Int = 1
  override fun setBacklog(backlog: Int) = this
  override fun setConnectTimeoutMillis(timeout: Int) = this
  override fun setPerformancePreferences(arg0: Int, arg1: Int, arg2: Int) = this
  override fun setAllocator(alloc: ByteBufAllocator) = withThis { super.setAllocator(alloc) }
  override fun setAutoRead(autoread: Boolean) = withThis { super.setAutoRead(true) }
  override fun setMessageSizeEstimator(est: MessageSizeEstimator) = withThis { super.setMessageSizeEstimator(est) }
  override fun setWriteSpinCount(spincount: Int) = withThis { super.setWriteSpinCount(spincount) }
  override fun setRecvByteBufAllocator(alloc: RecvByteBufAllocator) =
    withThis { super.setRecvByteBufAllocator(alloc) }

  override fun setWriteBufferHighWaterMark(writeBufferHighWaterMark: Int) =
    super.setWriteBufferHighWaterMark(writeBufferHighWaterMark) as ServerSocketChannelConfig

  override fun setWriteBufferLowWaterMark(writeBufferLowWaterMark: Int) =
    super.setWriteBufferLowWaterMark(writeBufferLowWaterMark) as ServerSocketChannelConfig

  override fun setWriteBufferWaterMark(writeBufferWaterMark: WriteBufferWaterMark) =
    super.setWriteBufferWaterMark(writeBufferWaterMark) as ServerSocketChannelConfig

  override fun getReceiveBufferSize(): Int = try {
    datagramChannel.socket().receiveBufferSize
  } catch (ex: SocketException) {
    throw ChannelException(ex)
  }

  override fun setReceiveBufferSize(size: Int) = try {
    datagramChannel.socket().receiveBufferSize = size
    this
  } catch (ex: SocketException) {
    throw ChannelException(ex)
  }

  override fun isReuseAddress(): Boolean = try {
    datagramChannel.socket().reuseAddress
  } catch (ex: SocketException) {
    throw ChannelException(ex)
  }

  override fun setReuseAddress(reuseaddr: Boolean) = try {
    datagramChannel.socket().reuseAddress = true
    this
  } catch (ex: SocketException) {
    throw ChannelException(ex)
  }
}