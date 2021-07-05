package cn.tursom.core.netty

import io.netty.buffer.ByteBufAllocator
import io.netty.channel.*
import io.netty.util.Attribute
import io.netty.util.AttributeKey
import java.net.SocketAddress

class LazyChannel(
  val getChannel: () -> Channel?,
) : Channel {
  override fun <T : Any?> attr(key: AttributeKey<T>?): Attribute<T> {
    return getChannel()!!.attr(key)
  }

  override fun <T : Any?> hasAttr(key: AttributeKey<T>?): Boolean {
    return getChannel()!!.hasAttr(key)
  }

  override fun bind(localAddress: SocketAddress?): ChannelFuture {
    return getChannel()!!.bind(localAddress)
  }

  override fun bind(localAddress: SocketAddress?, promise: ChannelPromise?): ChannelFuture {
    return getChannel()!!.bind(localAddress, promise)
  }

  override fun connect(remoteAddress: SocketAddress?): ChannelFuture {
    return getChannel()!!.connect(remoteAddress)
  }

  override fun connect(remoteAddress: SocketAddress?, localAddress: SocketAddress?): ChannelFuture {
    return getChannel()!!.connect(remoteAddress, localAddress)
  }

  override fun connect(remoteAddress: SocketAddress?, promise: ChannelPromise?): ChannelFuture {
    return getChannel()!!.connect(remoteAddress, promise)
  }

  override fun connect(
    remoteAddress: SocketAddress?,
    localAddress: SocketAddress?,
    promise: ChannelPromise?,
  ): ChannelFuture {
    return getChannel()!!.connect(remoteAddress, localAddress, promise)
  }

  override fun disconnect(): ChannelFuture {
    return getChannel()!!.disconnect()
  }

  override fun disconnect(promise: ChannelPromise?): ChannelFuture {
    return getChannel()!!.disconnect(promise)
  }

  override fun close(): ChannelFuture {
    return getChannel()!!.close()
  }

  override fun close(promise: ChannelPromise?): ChannelFuture {
    return getChannel()!!.close(promise)
  }

  override fun deregister(): ChannelFuture {
    return getChannel()!!.deregister()
  }

  override fun deregister(promise: ChannelPromise?): ChannelFuture {
    return getChannel()!!.deregister(promise)
  }

  override fun read(): Channel {
    return getChannel()!!.read()
  }

  override fun write(msg: Any?): ChannelFuture {
    return getChannel()!!.write(msg)
  }

  override fun write(msg: Any?, promise: ChannelPromise?): ChannelFuture {
    return getChannel()!!.write(msg, promise)
  }

  override fun flush(): Channel {
    return getChannel()!!.flush()
  }

  override fun writeAndFlush(msg: Any?, promise: ChannelPromise?): ChannelFuture {
    return getChannel()!!.writeAndFlush(msg, promise)
  }

  override fun writeAndFlush(msg: Any?): ChannelFuture {
    return getChannel()!!.writeAndFlush(msg)
  }

  override fun newPromise(): ChannelPromise {
    return getChannel()!!.newPromise()
  }

  override fun newProgressivePromise(): ChannelProgressivePromise {
    return getChannel()!!.newProgressivePromise()
  }

  override fun newSucceededFuture(): ChannelFuture {
    return getChannel()!!.newSucceededFuture()
  }

  override fun newFailedFuture(cause: Throwable?): ChannelFuture {
    return getChannel()!!.newFailedFuture(cause)
  }

  override fun voidPromise(): ChannelPromise {
    return getChannel()!!.voidPromise()
  }

  override fun compareTo(other: Channel?): Int {
    return getChannel()!!.compareTo(other)
  }

  override fun id(): ChannelId {
    return getChannel()!!.id()
  }

  override fun eventLoop(): EventLoop {
    return getChannel()!!.eventLoop()
  }

  override fun parent(): Channel {
    return getChannel()!!.parent()
  }

  override fun config(): ChannelConfig {
    return getChannel()!!.config()
  }

  override fun isOpen(): Boolean {
    return getChannel()!!.isOpen
  }

  override fun isRegistered(): Boolean {
    return getChannel()!!.isRegistered
  }

  override fun isActive(): Boolean {
    return getChannel()!!.isActive
  }

  override fun metadata(): ChannelMetadata {
    return getChannel()!!.metadata()
  }

  override fun localAddress(): SocketAddress {
    return getChannel()!!.localAddress()
  }

  override fun remoteAddress(): SocketAddress {
    return getChannel()!!.remoteAddress()
  }

  override fun closeFuture(): ChannelFuture {
    return getChannel()!!.closeFuture()
  }

  override fun isWritable(): Boolean {
    return getChannel()!!.isWritable
  }

  override fun bytesBeforeUnwritable(): Long {
    return getChannel()!!.bytesBeforeUnwritable()
  }

  override fun bytesBeforeWritable(): Long {
    return getChannel()!!.bytesBeforeWritable()
  }

  override fun unsafe(): Channel.Unsafe {
    return getChannel()!!.unsafe()
  }

  override fun pipeline(): ChannelPipeline {
    return getChannel()!!.pipeline()
  }

  override fun alloc(): ByteBufAllocator {
    return getChannel()!!.alloc()
  }
}
