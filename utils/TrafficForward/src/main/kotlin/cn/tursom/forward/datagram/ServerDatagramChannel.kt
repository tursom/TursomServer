package cn.tursom.forward.datagram

import io.netty.channel.Channel
import io.netty.channel.ChannelMetadata
import io.netty.channel.ChannelOutboundBuffer
import io.netty.channel.nio.AbstractNioMessageChannel
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.ServerSocketChannel
import io.netty.util.internal.PlatformDependent
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.net.StandardProtocolFamily
import java.nio.channels.SelectionKey
import java.nio.channels.spi.SelectorProvider
import java.util.concurrent.ConcurrentHashMap

class ServerDatagramChannel constructor(
  datagramChannel: java.nio.channels.DatagramChannel
) : AbstractNioMessageChannel(null, datagramChannel, SelectionKey.OP_READ), ServerSocketChannel {
  constructor() : this(SelectorProvider.provider().openDatagramChannel(StandardProtocolFamily.INET))

  private val metadata = ChannelMetadata(true)
  private val config: ServerDatagramChannelConfig = ServerDatagramChannelConfig(this, datagramChannel)
  private val channels = ConcurrentHashMap<InetSocketAddress, DatagramChannel>()
  override fun localAddress(): InetSocketAddress = super.localAddress() as InetSocketAddress
  public override fun localAddress0(): SocketAddress = javaChannel().socket().localSocketAddress
  override fun remoteAddress() = null
  override fun remoteAddress0() = null
  override fun metadata() = metadata
  override fun config() = config
  override fun isActive(): Boolean = javaChannel().isOpen && javaChannel().socket().isBound
  override fun javaChannel() = super.javaChannel() as java.nio.channels.DatagramChannel
  override fun doBind(localAddress: SocketAddress) = javaChannel().socket().bind(localAddress)
  override fun doConnect(addr1: SocketAddress, addr2: SocketAddress) = throw UnsupportedOperationException()
  override fun doFinishConnect() = throw UnsupportedOperationException()
  override fun doDisconnect() = throw UnsupportedOperationException()

  override fun doClose() {
    for (channel in channels.values) channel.close()
    javaChannel().close()
  }

  fun removeChannel(channel: Channel) {
    if (channel is DatagramChannel) {
      eventLoop().submit {
        val remote = channel.remoteAddress() as InetSocketAddress
        if (channels[remote] === channel) {
          channels.remove(remote)
        }
      }
    }
  }

  override fun doReadMessages(list: MutableList<Any>): Int {
    val javaChannel = javaChannel()
    val allocatorHandle = unsafe().recvBufAllocHandle()
    val buffer = allocatorHandle.allocate(config.allocator)
    allocatorHandle.attemptedBytesRead(buffer.writableBytes())
    var freeBuffer = true
    return try {
      val nioBuffer = buffer.internalNioBuffer(buffer.writerIndex(), buffer.writableBytes())
      val nioPos = nioBuffer.position()
      val socketAddress = javaChannel.receive(nioBuffer) ?: return 0
      allocatorHandle.lastBytesRead(nioBuffer.position() - nioPos)
      buffer.writerIndex(buffer.writerIndex() + allocatorHandle.lastBytesRead())

      var udpChannel = channels[socketAddress as InetSocketAddress]
      if (udpChannel == null || !udpChannel.isOpen) {
        udpChannel = DatagramChannel(this, socketAddress)
        channels[socketAddress] = udpChannel
        list.add(udpChannel)
        udpChannel.addBuffer(buffer)
        freeBuffer = false
        1
      } else {
        udpChannel.addBuffer(buffer)
        freeBuffer = false
        if (udpChannel.isRegistered) udpChannel.read()
        0
      }
    } catch (t: Throwable) {
      PlatformDependent.throwException(t)
      -1
    } finally {
      if (freeBuffer) buffer.release()
    }
  }

  override fun doWriteMessage(msg: Any, buffer: ChannelOutboundBuffer): Boolean {
    if (msg !is DatagramPacket) return false
    val recipient = msg.recipient()
    val byteBuf = msg.content()
    val readableBytes = byteBuf.readableBytes()
    if (readableBytes == 0) return true
    val internalNioBuffer = byteBuf.internalNioBuffer(
      byteBuf.readerIndex(), readableBytes
    )
    return javaChannel().send(internalNioBuffer, recipient) > 0
  }
}