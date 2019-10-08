package cn.tursom.utils.pool

import cn.tursom.core.pool.AsyncPool
import cn.tursom.utils.asynclock.AsyncMutexLock

/**
 * 这个缓冲池不会释放自己持有的节点资源，防止频繁分配对象导致性能下降
 */
class AsyncCachedLinkedPool<T> : AsyncPool<T> {
	@Volatile
	private var rootNode: Node<T>? = null
	@Volatile
	private var cacheNode: Node<T>? = null
	private val lock = AsyncMutexLock()
	
	override suspend fun put(cache: T): Boolean = lock {
		rootNode = if (cacheNode != null) {
			val node = cacheNode!!
			cacheNode = node.next
			node.next = rootNode
			rootNode = node
			node
		} else {
			Node(cache, rootNode)
		}
		true
	}
	
	override suspend fun get(): T? = lock {
		val node = rootNode
		rootNode = rootNode?.next
		val value = node?.value
		node?.value = null
		node?.next = cacheNode
		cacheNode = node
		value
	}
	
	private class Node<T>(var value: T? = null, var next: Node<T>? = null)
}
