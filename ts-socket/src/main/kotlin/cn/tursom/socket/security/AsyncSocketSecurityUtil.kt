package cn.tursom.socket.security

import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.encrypt.AES
import cn.tursom.core.encrypt.PublicKeyEncrypt
import cn.tursom.core.encrypt.RSA
import cn.tursom.core.pool.HeapMemoryPool
import cn.tursom.socket.AsyncSocket
import cn.tursom.channel.enhance.EnhanceChannel
import cn.tursom.channel.enhance.impl.ByteArrayWriter
import cn.tursom.channel.enhance.impl.ChannelReaderImpl
import cn.tursom.channel.enhance.impl.ChannelWriterImpl

object AsyncSocketSecurityUtil {
  private val memoryPool = HeapMemoryPool(4096)

  suspend fun initActiveAESSocket(socket: AsyncSocket, rsa: PublicKeyEncrypt): EnhanceChannel<ByteArray, ByteArray> {
    // 发送RSA公钥
    socket.write(HeapByteBuffer(rsa.publicKey!!.encoded))
    // 接受AES密钥
    val aesKey = socket.read(memoryPool)
    val aes = AES(aesKey.getBytes())
    return SecurityEnhanceChannel(ChannelReaderImpl(socket), ByteArrayWriter(ChannelWriterImpl(socket)), aes)
  }

  suspend fun initPassiveAESSocket(
    socket: AsyncSocket,
    publicKeyEncryptBuilder: (key: ByteArray) -> PublicKeyEncrypt = { RSA(it) }
  ): EnhanceChannel<ByteArray, ByteArray> {
    // 接受RSA公钥
    val rsaKey = socket.read(memoryPool)
    val rsaInstance = publicKeyEncryptBuilder(rsaKey.getBytes())
    // 生成AES对象
    val aes = AES()
    // 对AES密钥使用RSA公钥进行加密
    val aesKey = HeapByteBuffer(rsaInstance.encrypt(aes.secKey.encoded))
    socket.write(aesKey)
    return SecurityEnhanceChannel(ChannelReaderImpl(socket), ByteArrayWriter(ChannelWriterImpl(socket)), aes)
  }
}

