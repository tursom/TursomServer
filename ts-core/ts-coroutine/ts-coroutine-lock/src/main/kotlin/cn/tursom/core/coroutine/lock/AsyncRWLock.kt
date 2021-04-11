package cn.tursom.core.coroutine.lock

interface AsyncRWLock : AsyncLock {
  suspend fun <T> doRead(block: suspend () -> T): T
  suspend fun <T> doWrite(block: suspend () -> T): T
}