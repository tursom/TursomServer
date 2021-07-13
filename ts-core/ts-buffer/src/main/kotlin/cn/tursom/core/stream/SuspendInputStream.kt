package cn.tursom.core.stream

import cn.tursom.core.buffer.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface SuspendInputStream : InputStream {
  fun skip(n: Long, handler: (Throwable?) -> Unit)
  fun read(handler: (Int, Throwable?) -> Unit)
  fun read(buffer: ByteArray, offset: Int = 0, len: Int = buffer.size - offset, handler: (Int, Throwable?) -> Unit)
  fun read(buffer: ByteBuffer, handler: (Throwable?) -> Unit)

  suspend fun suspendSkip(n: Long) {
    suspendCoroutine<Unit> { cont ->
      skip(n) {
        cont.resume(Unit)
      }
    }
  }

  suspend fun suspendRead(): Int {
    return suspendCoroutine { cont ->
      read { it, e ->
        if (e != null) {
          cont.resumeWithException(e)
        } else {
          cont.resume(it)
        }
      }
    }
  }

  suspend fun suspendRead(buffer: ByteArray, offset: Int = 0, len: Int = buffer.size - offset): Int {
    return suspendCoroutine { cont ->
      read(buffer, offset, len) { it, e ->
        if (e != null) {
          cont.resumeWithException(e)
        } else {
          cont.resume(it)
        }
      }
    }
  }

  suspend fun suspendRead(buffer: ByteBuffer) {
    suspendCoroutine<Unit> { cont ->
      read(buffer) { e ->
        if (e != null) {
          cont.resumeWithException(e)
        } else {
          cont.resume(Unit)
        }
      }
    }
  }
}