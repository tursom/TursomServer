package cn.tursom.core.pool

import cn.tursom.core.buffer.ByteBuffer

/**
 * 内存池
 */
interface MemoryPool {
  val staticSize: Boolean get() = true
  var autoCollection: Boolean
    get() = false
    set(_) {}

  //  fun allocate(): Int
  fun free(memory: ByteBuffer)
  fun free(token: Int) = Unit

  fun getMemory(): ByteBuffer
  fun getMemoryOrNull(): ByteBuffer?

  override fun toString(): String

  fun get() = getMemory()

  operator fun get(blockCount: Int): Array<ByteBuffer> = Array(blockCount) { get() }

  fun gc() {}
}

inline operator fun <T> MemoryPool.invoke(action: (ByteBuffer) -> T): T {
  return getMemory().use { buffer ->
    action(buffer)
  }
}
