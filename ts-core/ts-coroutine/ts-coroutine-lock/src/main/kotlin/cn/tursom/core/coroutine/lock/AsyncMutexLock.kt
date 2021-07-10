package cn.tursom.core.coroutine.lock

import java.util.concurrent.atomic.AtomicBoolean

class AsyncMutexLock : AsyncLock {
  private val lock = AtomicBoolean(false)
  private val waitList = AsyncWaitList()

  suspend fun wait() {
    var loopTime = 20
    while (loopTime-- > 0) if (!lock.get()) return
    waitList.wait()
    waitList.resume()
  }

  override suspend fun sync(block: suspend () -> Unit) {
    invoke(block)
  }

  override suspend fun isLock(): Boolean {
    return lock.get()
  }

  override suspend operator fun <T> invoke(block: suspend () -> T): T {
    lock.lock()
    try {
      return block()
    } finally {
      lock.release()
    }
  }

  private suspend fun AtomicBoolean.lock() {
    var loopTime = 20
    while (loopTime-- > 0) if (compareAndSet(false, true)) return
    waitList.wait()
  }

  override suspend fun AtomicBoolean.release() {
    if (waitList.notEmpty) {
      waitList.resume()
    } else {
      set(false)
    }
  }
}

