package cn.tursom.core.coroutine.lock

import java.util.concurrent.atomic.AtomicInteger

/**
 * 读优化锁
 */
@Suppress("MemberVisibilityCanBePrivate")
class AsyncReadFirstRWLock() : AsyncRWLock {
  private val lock = AsyncMutexLock()
  private val readNumber = AtomicInteger(0)
  private val writeList = AsyncWaitList()

  override suspend fun <T> doRead(block: suspend () -> T): T {
    readNumber.incrementAndGet()
    lock.wait()
    try {
      return block()
    } finally {
      readNumber.decrementAndGet()
      if (readNumber.get() == 0) writeList.resume()
    }
  }

  override suspend fun <T> doWrite(block: suspend () -> T): T {
    return invoke(block)
  }

  override suspend fun sync(block: suspend () -> Unit) {
    invoke(block)
  }

  override suspend fun <T> invoke(block: suspend () -> T): T {
    while (readNumber.get() != 0) writeList.wait()
    return lock { block() }
  }

  override suspend fun isLock(): Boolean {
    return lock.isLock()
  }
}
