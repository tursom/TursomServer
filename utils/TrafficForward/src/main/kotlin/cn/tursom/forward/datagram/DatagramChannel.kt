package cn.tursom.forward.datagram

import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.channel.socket.DatagramPacket
import io.netty.util.ReferenceCountUtil
import io.netty.util.internal.RecyclableArrayList
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

class DatagramChannel constructor(
  private val serverChannel: ServerDatagramChannel,
  private val remote: InetSocketAddress
) : AbstractChannel(serverChannel) {
  @Volatile
  private var open = true
  private var reading = AtomicBoolean(false)
  private val metadata = ChannelMetadata(false)
  private val config = DefaultChannelConfig(this)
  private val buffers = ConcurrentLinkedQueue<ByteBuf>()
  override fun metadata(): ChannelMetadata = metadata
  override fun config(): ChannelConfig = config
  override fun isActive(): Boolean = open
  override fun isOpen(): Boolean = isActive
  override fun doDisconnect() = doClose()
  internal fun addBuffer(buffer: ByteBuf) = buffers.add(buffer)
  override fun isCompatible(eventloop: EventLoop): Boolean = eventloop is DefaultEventLoop
  override fun newUnsafe(): AbstractUnsafe = UdpChannelUnsafe()
  override fun localAddress0(): SocketAddress = serverChannel.localAddress0()
  override fun remoteAddress0(): SocketAddress = remote
  override fun doBind(addr: SocketAddress) = throw UnsupportedOperationException()

  override fun doClose() {
    open = false
    serverChannel.removeChannel(this)
  }

  override fun doBeginRead() {
    if (!reading.compareAndSet(false, true)) return
    try {
      while (!buffers.isEmpty()) {
        val buffer = buffers.poll() ?: continue
        pipeline().fireChannelRead(buffer)
      }
      pipeline().fireChannelReadComplete()
    } finally {
      reading.set(false)
    }
  }

  override fun doWrite(buffer: ChannelOutboundBuffer) {
    val list = RecyclableArrayList.newInstance()
    var freeList = true
    try {
      while (!buffer.isEmpty) {
        val buf = buffer.current() as ByteBuf? ?: continue
        list.add(buf.retain())
        buffer.remove()
      }
      freeList = false
    } finally {
      if (freeList) {
        for (obj in list) {
          ReferenceCountUtil.safeRelease(obj)
        }
        list.recycle()
      }
    }
    serverChannel.eventLoop().execute {
      try {
        for (buf in list) {
          if (buf is ByteBuf) {
            serverChannel.unsafe().write(DatagramPacket(buf, remote), voidPromise())
          }
        }
        serverChannel.unsafe().flush()
      } finally {
        list.recycle()
      }
    }
  }

  private inner class UdpChannelUnsafe : AbstractUnsafe() {
    override fun connect(
      remoteAddress: SocketAddress?,
      localAddress: SocketAddress?,
      promise: ChannelPromise?
    ) = throw UnsupportedOperationException()

  }

}