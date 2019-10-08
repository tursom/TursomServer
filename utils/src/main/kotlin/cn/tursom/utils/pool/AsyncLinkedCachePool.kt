package cn.tursom.utils.pool

import cn.tursom.core.pool.AsyncPool
import cn.tursom.utils.asynclock.AsyncMutexLock

class AsyncLinkedCachePool<T> : AsyncPool<T> {
	@Volatile
	private var rootNode: Node<T>? = null
	private val lock = AsyncMutexLock()
	
	override suspend fun put(cache: T): Boolean = lock {
		rootNode = Node(cache, rootNode)
		true
	}
	
	override suspend fun get(): T? = lock {
		val node = rootNode
		rootNode = rootNode?.next
		node?.value
	}
	
	private class Node<T>(var value: T? = null, var next: Node<T>? = null)
}