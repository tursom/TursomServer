package cn.tursom.utils.asynclock

import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

interface AsyncLock {
	suspend fun sync(block: suspend () -> Unit)
	suspend fun isLock(): Boolean
	suspend operator fun <T> invoke(block: suspend () -> T): T
	
	suspend fun AtomicBoolean.lock(delayTime: Long) {
		// 如果得不到锁，先自旋20次
		var maxLoopTime = 20
		while (maxLoopTime-- > 0) {
			if (compareAndSet(false, true)) return
		}
		while (!compareAndSet(false, true)) {
			delay(delayTime)
		}
	}
	
	suspend fun AtomicBoolean.release() {
		set(false)
	}
	
	suspend fun AtomicBoolean.wait(delayTime: Long) {
		// 如果得不到锁，先自旋20次
		var maxLoopTime = 20
		while (maxLoopTime-- > 0) {
			if (!get()) return
		}
		while (get()) {
			delay(delayTime)
		}
	}
	
	suspend fun AsyncLock.wait(delayTime: Long) {
		// 如果得不到锁，先自旋20次
		var maxLoopTime = 20
		while (maxLoopTime-- > 0) {
			if (!isLock()) return
		}
		while (isLock()) {
			delay(delayTime)
		}
	}
	
	suspend fun AtomicInteger.wait(delayTime: Long) {
		// 如果得不到锁，先自旋20次
		var maxLoopTime = 20
		while (maxLoopTime-- > 0) {
			if (get() <= 0) return
		}
		while (get() > 0) {
			delay(delayTime)
		}
	}
}