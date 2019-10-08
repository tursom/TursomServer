package cn.tursom.core.datastruct

class SetMap<K>(private val set: Set<K>) : Map<K, Unit> {

	override val keys: Set<K>
		get() = set
	override val size: Int
		get() = set.size
	override val values: Collection<Unit> = listOf()

	override fun containsKey(key: K): Boolean {
		return set.contains(key)
	}

	override fun containsValue(value: Unit): Boolean {
		return true
	}

	override fun get(key: K): Unit? {
		return if (set.contains(key)) Unit else null
	}

	override fun isEmpty(): Boolean {
		return size == 0
	}

	override val entries: Set<Map.Entry<K, Unit>> = object : Set<Map.Entry<K, Unit>> {
		override val size: Int
			get() = set.size

		override fun contains(element: Map.Entry<K, Unit>): Boolean {
			return set.contains(element.key)
		}

		override fun containsAll(elements: Collection<Map.Entry<K, Unit>>): Boolean {
			elements.forEach {
				if (!set.contains(it.key)) return false
			}
			return true
		}

		override fun isEmpty(): Boolean {
			return size == 0
		}

		override fun iterator(): Iterator<Map.Entry<K, Unit>> {
			return SetMapIterator(set)
		}
	}

	class SetMapIterator<K>(set: Set<K>) : Iterator<Map.Entry<K, Unit>> {
		private val iterator = set.iterator()
		override fun hasNext(): Boolean {
			return iterator.hasNext()
		}

		override fun next(): Map.Entry<K, Unit> {
			return Entry(iterator.next())
		}
	}

	data class Entry<K>(override val key: K) : Map.Entry<K, Unit> {
		override val value: Unit
			get() = Unit
	}
}