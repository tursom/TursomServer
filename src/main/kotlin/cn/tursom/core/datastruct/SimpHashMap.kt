package cn.tursom.core.datastruct

class SimpHashMap<K, V>(
	val hashMap: java.util.AbstractMap<K, V> = HashMap()
) : SimpMap<K, V>, Map<K, V> by hashMap {
	override fun delete(key: K): V? {
		return hashMap.remove(key)
	}

	override fun set(key: K, value: V) {
		hashMap[key] = value
	}

	override fun clear() {
		hashMap.clear()
	}

	override fun first(): V? {
		val i = hashMap.iterator()
		return if (i.hasNext()) {
			i.next().value
		} else {
			null
		}
	}
}