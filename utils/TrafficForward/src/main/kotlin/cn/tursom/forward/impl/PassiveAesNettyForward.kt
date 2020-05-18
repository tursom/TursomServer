package cn.tursom.forward.impl

import cn.tursom.core.encrypt.AES
import cn.tursom.core.encrypt.PublicKeyEncrypt
import cn.tursom.core.encrypt.RSA
import cn.tursom.forward.Forward
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext

open class PassiveAesNettyForward(
  channel: Channel,
  forward: Forward? = null,
  private val aes: AES = AES(),
  private val publicKeyEncryptBuilder: (ByteArray) -> PublicKeyEncrypt = { RSA(it) }
) : NettyForward(channel, forward) {
  protected var recvBytesHandler: (buf: ByteBuf, ctx: ChannelHandlerContext) -> Unit = ::recvAESKey

  override fun recvBytes(buf: ByteBuf, ctx: ChannelHandlerContext) {
    val buffer = ByteArray(buf.readableBytes())
    buf.readBytes(buffer)
    recvBytesHandler(Unpooled.wrappedBuffer(aes.decrypt(buffer)), ctx)
  }

  private fun superRecvBytes(buf: ByteBuf, ctx: ChannelHandlerContext) = super.recvBytes(buf, ctx)
  private fun recvAESKey(buf: ByteBuf, ctx: ChannelHandlerContext) {
    val key = ByteArray(buf.readableBytes())
    buf.readBytes(key)
    val publicKeyEncrypt = publicKeyEncryptBuilder(key)
    write(publicKeyEncrypt.encrypt(aes.secKey.encoded))
    recvBytesHandler = ::superRecvBytes
  }
}