package cn.tursom.socket.security

import cn.tursom.channel.enhance.ChannelReader
import cn.tursom.channel.enhance.ChannelWriter
import cn.tursom.channel.enhance.EnhanceChannel
import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.encrypt.AES
import cn.tursom.core.pool.MemoryPool

class SecurityEnhanceChannel(
  val preReader: ChannelReader<ByteBuffer>,
  val preWriter: ChannelWriter<ByteArray>,
  val aes: AES
) : EnhanceChannel<ByteArray, ByteArray> {
  override val reader: ChannelReader<ByteArray> get() = this
  override val writer: ChannelWriter<ByteArray> get() = this

  override fun close() {
    preReader.close()
    preWriter.close()
  }

  override suspend fun read(pool: MemoryPool, timeout: Long): ByteArray {
    val buffer = preReader.read(pool, timeout)
    return aes.decrypt(buffer)
  }

  override suspend fun write(value: ByteArray) {
    preWriter.write(aes.encrypt(value))
  }

  override suspend fun flush(timeout: Long): Long = preWriter.flush()
}