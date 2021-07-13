package cn.tursom.core.stream.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.stream.SuspendInputStream
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

class ByteBufferInputStream(
  val buffer: ByteBuffer,
  private val lock: Lock? = null,
) : SuspendInputStream {
  private var closed = false

  private inline fun <R> withLock(func: () -> R): R {
    return if (lock != null) {
      lock.withLock(func)
    } else {
      func()
    }
  }

  override val available: Int get() = buffer.readable
  override fun skip(n: Long, handler: (Throwable?) -> Unit) {
    handler(try {
      skip(n)
      null
    } catch (e: Exception) {
      e
    })
  }

  override fun skip(n: Long) {
    checkClosed {
      withLock {
        buffer.readPosition += n.toInt()
      }
    }
  }

  override fun read(handler: (Int, Throwable?) -> Unit) {
    var n = 0
    var t: Throwable? = null
    try {
      n = read()
    } catch (e: Throwable) {
      t = e
    }
    handler(n, t)
  }

  override fun read(buffer: ByteArray, offset: Int, len: Int, handler: (Int, Throwable?) -> Unit) {
    var n = 0
    var t: Throwable? = null
    try {
      n = read(buffer, offset, len)
    } catch (e: Throwable) {
      t = e
    }
    handler(n, t)
  }

  override fun read(buffer: ByteBuffer, handler: (Throwable?) -> Unit) {
    handler(try {
      read(buffer)
      null
    } catch (e: Throwable) {
      e
    })
  }

  override fun read(): Int {
    return withLock {
      if (closed || buffer.readable == 0) -1 else buffer.get().toInt()
    }
  }

  override fun read(buffer: ByteArray, offset: Int, len: Int) = checkClosed {
    withLock {
      this.buffer.writeTo(buffer, offset, len)
    }
  } ?: -1

  override fun read(buffer: ByteBuffer) {
    checkClosed {
      withLock {
        this.buffer.writeTo(buffer)
      }
    }
  }

  private inline fun <T> checkClosed(action: () -> T): T? = if (!closed) action() else null

  override fun close() {
    closed = true
  }
}