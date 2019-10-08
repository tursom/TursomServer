package cn.tursom.core.datastruct.async.interfaces

interface AsyncMap<K, V> : AsyncCollection<Map.Entry<K, V>> {
	override val size: Int
	val entries: AsyncSet<Map.Entry<K, V>>
	val keys: AsyncSet<K>
	val values: AsyncCollection<V>

	suspend infix fun containsKey(key: K): Boolean
	suspend infix fun containsValue(value: V): Boolean
	suspend infix fun get(key: K): V?

	override suspend fun isEmpty(): Boolean
	suspend fun isNotEmpty(): Boolean = !isEmpty()

	override suspend fun contains(element: Map.Entry<K, V>): Boolean {
		return get(element.key) == element.value
	}

	override suspend fun containsAll(elements: AsyncCollection<Map.Entry<K, V>>): Boolean {
		return elements.forEach { contains(it) }
	}
}

