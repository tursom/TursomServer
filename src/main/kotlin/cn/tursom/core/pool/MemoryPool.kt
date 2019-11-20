package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer

/**
 * 内存池
 */
interface MemoryPool {
  val staticSize: Boolean get() = true

  //  fun allocate(): Int
  fun free(memory: ByteBuffer)

  fun getMemory(): ByteBuffer
  fun getMemoryOrNull(): ByteBuffer?

  override fun toString(): String

  suspend operator fun <T> invoke(action: suspend (ByteBuffer) -> T): T {
    return getMemory().use { buffer ->
      action(buffer)
    }
  }

  fun get() = getMemory()

  operator fun get(blockCount: Int): Array<ByteBuffer> = Array(blockCount) { get() }

  fun gc() {}
}
