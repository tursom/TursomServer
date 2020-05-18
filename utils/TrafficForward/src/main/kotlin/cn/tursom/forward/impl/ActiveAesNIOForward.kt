package cn.tursom.forward.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.encrypt.AES
import cn.tursom.core.encrypt.PublicKeyEncrypt
import cn.tursom.core.encrypt.RSA
import cn.tursom.forward.Forward
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.channels.SelectableChannel

class ActiveAesNIOForward(
  host: SocketAddress,
  channel: SelectableChannel = udp(),
  timeout: Long = 3,
  forward: Forward? = null,
  publicKeyEncrypt: PublicKeyEncrypt = RSA()
) : NIOForward(host, channel, timeout, forward) {
  constructor(
    host: String,
    port: Int,
    channel: SelectableChannel = udp(),
    timeout: Long = 3,
    forward: Forward? = null,
    publicKeyEncrypt: PublicKeyEncrypt = RSA()
  ) : this(InetSocketAddress(host, port), channel, timeout, forward, publicKeyEncrypt)

  private lateinit var aes: AES

  protected var recvMsgHandler: (ByteBuffer) -> Unit = ::recvAESKey
  private lateinit var publicKeyEncrypt: PublicKeyEncrypt

  init {
    @Suppress("LeakingThis")
    write(publicKeyEncrypt.publicKey!!.encoded)
  }

  override fun recvMsg(msg: ByteBuffer) {
    recvMsgHandler(msg)
  }

  private fun superRecvMsg(msg: ByteBuffer) {
    super.recvMsg(HeapByteBuffer(publicKeyEncrypt.decrypt(msg.toByteArray())))
  }

  private fun recvAESKey(buf: ByteBuffer) {
    aes = AES(publicKeyEncrypt.decrypt(buf))
    recvMsgHandler = ::superRecvMsg
  }
}