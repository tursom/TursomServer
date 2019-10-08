package cn.tursom.utils.datastruct.async

import cn.tursom.utils.asynclock.AsyncReadFirstRWLock

class AsyncLinkSlot<K, V> {
	private val lock = AsyncReadFirstRWLock()
	@Volatile
	private var node: Node<K, V>? = null
	@Volatile
	private var rSize = 0
	
	val size: Int
		get() = rSize
	
	suspend fun clear() {
		lock.doWrite {
			node = null
			rSize = 0
		}
	}
	
	suspend fun put(key: K, value: V) {
		node?.forEach {
			if (it.key == key) {
				it.value = value
				return
			}
		}
		lock.doWrite {
			node = Node(key, value, node)
			node?.prev = node
			rSize++
		}
	}
	
	suspend fun remove(key: K): V? {
		return lock doWrite@{
			node?.forEach {
				if (it.key == key) {
					if (it == node) {
						node = it.next
						it.next?.prev = null
					} else {
						it.prev?.next = it.next
						it.next?.prev = it.prev
					}
					rSize--
					return@doWrite it.value
				}
			}
			null
		}
	}
	
	suspend fun containsKey(key: K): Boolean {
		return lock.doRead {
			node?.forEach {
				if (it.key == key) {
					return@doRead true
				}
			}
			false
		}
	}
	
	suspend fun containsValue(value: V): Boolean {
		return lock.doRead {
			node?.forEach {
				if (it.value == value) {
					return@doRead true
				}
			}
			false
		}
	}
	
	suspend fun get(key: K): V? {
		return lock.doRead {
			node?.forEach {
				if (it.key == key) {
					return@doRead it.value
				}
			}
			null
		}
	}
	
	fun isEmpty(): Boolean {
		return node == null
	}
	
	private data class Node<K, V>(
		val key: K,
		var value: V,
		var next: Node<K, V>?,
		var prev: Node<K, V>? = null
	) : Iterable<Node<K, V>> {
		override fun iterator(): Iterator<Node<K, V>> {
			return NodeIterator(this)
		}
	}
	
	private class NodeIterator<K, V>(private var node: Node<K, V>?) : Iterator<Node<K, V>> {
		
		override fun hasNext(): Boolean {
			return node != null
		}
		
		override fun next(): Node<K, V> {
			val thisNode = node!!
			node = node?.next
			return thisNode
		}
	}
}