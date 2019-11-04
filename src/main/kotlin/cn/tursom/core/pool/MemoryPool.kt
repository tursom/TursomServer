package cn.tursom.core.pool

import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.core.bytebuffer.NioAdvanceByteBuffer
import java.nio.ByteBuffer

/**
 * 内存池，提供批量的等大小的 ByteBuffer
 * 使用 allocate 分配内存，使用 getMemory 或 getAdvanceByteBuffer 获得内存，使用 free 释放内存
 */
interface MemoryPool {
  val blockSize: Int
  val blockCount: Int
  
  fun allocate(): Int
  fun free(token: Int)
  fun getMemory(token: Int): ByteBuffer?
  fun getAdvanceByteBuffer(token: Int): AdvanceByteBuffer? {
    val buffer = getMemory(token)
    return if (buffer != null) {
      NioAdvanceByteBuffer(buffer)
    } else {
      null
    }
  }
  
  override fun toString(): String
}


inline fun <T> MemoryPool.usingMemory(action: (ByteBuffer?) -> T): T {
  val token = allocate()
  return try {
    action(getMemory(token))
  } finally {
    free(token)
  }
}

inline fun <T> MemoryPool.usingAdvanceByteBuffer(action: (AdvanceByteBuffer?) -> T): T {
  val token = allocate()
  return try {
    action(getAdvanceByteBuffer(token))
  } finally {
    free(token)
  }
}