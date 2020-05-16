package cn.tursom.socket.security

import cn.tursom.core.buffer.impl.HeapByteBuffer
import cn.tursom.core.encrypt.AES
import cn.tursom.core.encrypt.RSA
import cn.tursom.core.pool.HeapMemoryPool
import cn.tursom.socket.AsyncSocket
import cn.tursom.socket.enhance.EnhanceSocket
import cn.tursom.socket.enhance.impl.ByteArrayWriter
import cn.tursom.socket.enhance.impl.SocketReaderImpl
import cn.tursom.socket.enhance.impl.SocketWriterImpl

object AsyncSocketSecurityUtil {
  private val memoryPool = HeapMemoryPool(4096)

  suspend fun initAESByRSAServer(socket: AsyncSocket, rsa: RSA): EnhanceSocket<ByteArray, ByteArray> {
    // 发送RSA公钥
    socket.write(HeapByteBuffer(rsa.publicKeyEncoded))
    // 接受AES密钥
    val aesKey = socket.read(memoryPool)
    val aes = AES(aesKey.getBytes())
    return SecurityEnhanceSocket(SocketReaderImpl(socket), ByteArrayWriter(SocketWriterImpl(socket)), aes)
  }

  suspend fun AsyncSocket.initAESByRSAClient(socket: AsyncSocket): EnhanceSocket<ByteArray, ByteArray> {
    // 接受RSA公钥
    val rsaKey = socket.read(memoryPool)
    val rsaInstance = RSA(rsaKey.getBytes())
    // 生成AES对象
    val aes = AES()
    // 对AES密钥使用RSA公钥进行加密
    val aesKey = HeapByteBuffer(rsaInstance.encrypt(aes.secKey.encoded))
    socket.write(aesKey)
    return SecurityEnhanceSocket(SocketReaderImpl(socket), ByteArrayWriter(SocketWriterImpl(socket)), aes)
  }
}
