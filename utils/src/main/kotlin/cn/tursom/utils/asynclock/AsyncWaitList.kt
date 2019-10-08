package cn.tursom.utils.asynclock

import cn.tursom.core.unsafe
import java.io.Closeable
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("MemberVisibilityCanBePrivate")
class AsyncWaitList : Closeable {
	val empty: Boolean get() = lockList == null
	val notEmpty: Boolean get() = lockList != null
	
	suspend fun wait() = suspendCoroutine<Int> { cont ->
		var list = lockList
		while (!unsafe.compareAndSwapObject(this, listOffset, list, LockNode(cont, list))) {
			list = lockList
		}
	}
	
	
	fun resume(): Boolean {
		var list = lockList ?: return false
		while (!unsafe.compareAndSwapObject(this, listOffset, list, list.next)) {
			list = lockList ?: return false
		}
		list.cont.resume(0)
		return true
	}
	
	fun resumeAll(): Boolean {
		var list: LockNode? = lockList ?: return false
		while (!unsafe.compareAndSwapObject(this, listOffset, list, null)) {
			list = lockList ?: return false
		}
		while (list != null) {
			list.cont.resume(0)
			list = list.next
		}
		return true
	}
	
	override fun close() {
		resumeAll()
	}
	
	@Volatile
	private var lockList: LockNode? = null
	//private val listLock = AsyncLoopLock()
	
	private data class LockNode(val cont: Continuation<Int>, val next: LockNode? = null)
	
	companion object {
		val listOffset = run {
			unsafe.objectFieldOffset(AsyncWaitList::class.java.getDeclaredField("lockList"))
		}
	}
}