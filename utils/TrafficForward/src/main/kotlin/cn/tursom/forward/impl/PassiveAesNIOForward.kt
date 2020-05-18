package cn.tursom.forward.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.encrypt.AES
import cn.tursom.core.encrypt.PublicKeyEncrypt
import cn.tursom.core.encrypt.RSA
import cn.tursom.forward.Forward
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.channels.SelectableChannel

/**
 * only support UDP(DatagramChannel) and TCP(SocketChannel)
 */
open class PassiveAesNIOForward(
  host: SocketAddress,
  channel: SelectableChannel = udp(),
  timeout: Long = 3,
  forward: Forward? = null,
  private val aes: AES = AES(),
  private val publicKeyEncryptBuilder: (publicKey: ByteArray) -> PublicKeyEncrypt = { RSA(it) }
) : NIOForward(host, channel, timeout, forward) {
  constructor(
    host: String,
    port: Int,
    channel: SelectableChannel = udp(),
    timeout: Long = 3,
    forward: Forward? = null,
    aes: AES = AES(),
    publicKeyEncryptBuilder: (publicKey: ByteArray) -> PublicKeyEncrypt = { RSA(it) }
  ) : this(InetSocketAddress(host, port), channel, timeout, forward, aes, publicKeyEncryptBuilder)

  protected var recvMsgHandler: (ByteBuffer) -> Unit = ::recvPublicKeyEncrypt
  private lateinit var publicKeyEncrypt: PublicKeyEncrypt

  override fun recvMsg(msg: ByteBuffer) {
    recvMsgHandler(msg)
  }

  private fun superRecvMsg(msg: ByteBuffer) {
    super.recvMsg(HeapByteBuffer(publicKeyEncrypt.decrypt(msg.toByteArray())))
  }

  private fun recvPublicKeyEncrypt(msg: ByteBuffer) {
    publicKeyEncrypt = publicKeyEncryptBuilder(msg.toByteArray())
    write(aes.secKey.encoded)
    recvMsgHandler = ::superRecvMsg
  }
}

