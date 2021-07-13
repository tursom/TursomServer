package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.SuspendOutputStream
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

class ByteBufferOutputStream(
  val buffer: ByteBuffer,
  private val lock: Lock? = null,
) : SuspendOutputStream {
  private var closed = false

  private inline fun <R> withLock(func: () -> R): R {
    return if (lock != null) {
      lock.withLock(func)
    } else {
      func()
    }
  }

  override fun write(byte: Byte) {
    checkClosed {
      withLock {
        buffer.put(byte)
      }
    }
  }

  override fun write(buffer: ByteArray, offset: Int, len: Int): Int = checkClosed {
    withLock {
      this.buffer.put(buffer, offset, len)
    }
  } ?: -1

  override fun write(buffer: ByteBuffer) {
    checkClosed {
      withLock {
        buffer.writeTo(this.buffer)
      }
    }
  }

  override suspend fun suspendWrite(byte: Byte) {
    write(byte)
  }

  override suspend fun suspendWrite(buffer: ByteBuffer) {
    write(buffer)
  }

  override fun flush() {}

  private inline fun <T> checkClosed(action: () -> T): T? = if (!closed) action() else null

  override fun close() {
    closed = true
  }
}

