package cn.tursom.core.stream

import cn.tursom.core.buffer.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface SuspendInputStream : InputStream {
  fun skip(n: Long, handler: () -> Unit)
  fun read(handler: (Int) -> Unit)
  fun read(buffer: ByteArray, handler: (Int) -> Unit)
  fun read(buffer: ByteArray, offset: Int, len: Int, handler: (Int) -> Unit)
  fun read(buffer: ByteBuffer, handler: () -> Unit)

  suspend fun suspendSkip(n: Long) {
    suspendCoroutine<Unit> { cont ->
      skip(n) {
        cont.resume(Unit)
      }
    }
  }

  suspend fun suspendRead(): Int {
    return suspendCoroutine { cont ->
      read {
        cont.resume(it)
      }
    }
  }

  suspend fun suspendRead(buffer: ByteArray) {
    suspendCoroutine<Unit> { cont ->
      read(buffer) {
        cont.resume(Unit)
      }
    }
  }

  suspend fun suspendRead(buffer: ByteArray, offset: Int, len: Int) {
    suspendCoroutine<Unit> { cont ->
      read(buffer, offset, len) {
        cont.resume(Unit)
      }
    }
  }

  suspend fun suspendRead(buffer: ByteBuffer) {
    suspendCoroutine<Unit> { cont ->
      read(buffer) {
        cont.resume(Unit)
      }
    }
  }
}