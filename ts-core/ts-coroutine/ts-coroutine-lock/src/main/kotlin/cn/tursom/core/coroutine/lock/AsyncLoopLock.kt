package cn.tursom.core.coroutine.lock

import java.util.concurrent.atomic.AtomicBoolean

class AsyncLoopLock : AsyncLock {
  private val lock = AtomicBoolean(false)

  override suspend fun sync(block: suspend () -> Unit) {
    invoke(block)
  }

  override suspend fun isLock(): Boolean = lock.get()

  override suspend fun <T> invoke(block: suspend () -> T): T {
    while (!lock.compareAndSet(false, true));
    try {
      return block()
    } finally {
      lock.set(false)
    }
  }
}