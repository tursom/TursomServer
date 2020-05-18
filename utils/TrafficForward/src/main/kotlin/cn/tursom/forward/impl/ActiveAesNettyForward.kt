package cn.tursom.forward.impl

import cn.tursom.core.encrypt.AES
import cn.tursom.core.encrypt.PublicKeyEncrypt
import cn.tursom.core.encrypt.RSA
import cn.tursom.forward.Forward
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext

open class ActiveAesNettyForward(
  channel: Channel,
  forward: Forward? = null,
  private val publicKeyEncrypt: PublicKeyEncrypt = RSA()
) : NettyForward(channel, forward) {
  protected var recvBytesHandler: (buf: ByteBuf, ctx: ChannelHandlerContext) -> Unit = ::recvAESKey
  private lateinit var aes: AES

  init {
    channel.writeAndFlush(Unpooled.wrappedBuffer(publicKeyEncrypt.publicKey!!.encoded))
  }

  override fun recvBytes(buf: ByteBuf, ctx: ChannelHandlerContext) {
    val buffer = ByteArray(buf.readableBytes())
    buf.readBytes(buffer)
    recvBytesHandler(Unpooled.wrappedBuffer(aes.decrypt(buffer)), ctx)
  }

  private fun superRecvBytes(buf: ByteBuf, ctx: ChannelHandlerContext) = super.recvBytes(buf, ctx)
  private fun recvAESKey(buf: ByteBuf, ctx: ChannelHandlerContext) {
    val key = ByteArray(buf.readableBytes())
    buf.readBytes(key)
    aes = AES(publicKeyEncrypt.decrypt(key))
    recvBytesHandler = ::superRecvBytes
  }
}

