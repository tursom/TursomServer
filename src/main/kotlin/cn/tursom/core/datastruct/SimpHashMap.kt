package cn.tursom.core.datastruct

class SimpHashMap<K, V>(
  val hashMap: java.util.AbstractMap<K, V> = HashMap()
) : SimpMap<K, V>, MutableMap<K, V> by hashMap {
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

	override fun put(key: K, value: V): V? {
		return hashMap.put(key, value)
	}

	override fun putAll(from: Map<out K, V>) {
		hashMap.putAll(from)
	}

	override fun remove(key: K): V? {
		return hashMap.remove(key)
	}
}