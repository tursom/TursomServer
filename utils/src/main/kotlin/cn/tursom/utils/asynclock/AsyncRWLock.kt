package cn.tursom.utils.asynclock

interface AsyncRWLock : AsyncLock {
	suspend fun <T> doRead(block: suspend () -> T): T
	suspend fun <T> doWrite(block: suspend () -> T): T
}