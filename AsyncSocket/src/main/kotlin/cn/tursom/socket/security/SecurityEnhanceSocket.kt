package cn.tursom.socket.security

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.encrypt.AES
import cn.tursom.core.pool.MemoryPool
import cn.tursom.socket.enhance.EnhanceSocket
import cn.tursom.socket.enhance.SocketReader
import cn.tursom.socket.enhance.SocketWriter

class SecurityEnhanceSocket(
  val reader: SocketReader<ByteBuffer>,
  val writer: SocketWriter<ByteArray>,
  val aes: AES
) : EnhanceSocket<ByteArray, ByteArray> {
  override fun close() {
    reader.close()
    writer.close()
  }

  override suspend fun read(pool: MemoryPool, timeout: Long): ByteArray {
    val buffer = reader.read(pool, timeout)
    return aes.decrypt(buffer)
  }

  override suspend fun write(value: ByteArray) {
    writer.write(aes.encrypt(value))
  }

  override suspend fun flush(timeout: Long): Long = writer.flush()
}