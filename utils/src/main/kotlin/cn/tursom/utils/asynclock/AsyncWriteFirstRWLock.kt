package cn.tursom.utils.asynclock

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Suppress("MemberVisibilityCanBePrivate")
class AsyncWriteFirstRWLock(val delayTime: Long = 10) : AsyncRWLock {
	
	private val lock = AtomicBoolean(false)
	private val readNumber = AtomicInteger(0)
	private val writeNumber = AtomicInteger(0)
	
	override suspend fun <T> doWrite(block: suspend () -> T): T {
		return invoke(block)
	}
	
	override suspend fun <T> doRead(block: suspend () -> T): T {
		// 先等待通知锁关闭
		writeNumber.wait(delayTime)
		
		// 添加读计数
		readNumber.incrementAndGet()
		
		try {
			return block()
		} finally {
			// 减少读计数
			readNumber.decrementAndGet()
		}
	}
	
	override suspend fun sync(block: suspend () -> Unit) {
		invoke(block)
	}
	
	override suspend fun <T> invoke(block: suspend () -> T): T {
		writeNumber.incrementAndGet()
		
		repeat(20) {}
		
		readNumber.wait(delayTime)
		
		lock.lock(delayTime)
		
		try {
			return block()
		} finally {
			lock.release()
			writeNumber.decrementAndGet()
		}
	}
	
	override suspend fun isLock(): Boolean {
		return lock.get()
	}
}