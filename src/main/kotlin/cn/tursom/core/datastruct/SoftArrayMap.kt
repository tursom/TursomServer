package cn.tursom.core.datastruct

import java.lang.ref.SoftReference

class SoftArrayMap<K, V>(val map: MutableMap<K, SoftReference<V>>) : MutableMap<K, V> {
    constructor(initialCapacity: Int = 16) : this(HashMap(initialCapacity))

    override val size: Int get() = map.size
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() = throw NotImplementedError()
    override val keys: MutableSet<K> get() = map.keys
    override val values: MutableCollection<V> get() = throw NotImplementedError()

    override fun put(key: K, value: V): V? = map.put(key, SoftReference(value))?.get()
    override fun remove(key: K): V? = map.remove(key)?.get()
    override fun clear() = map.clear()
    override fun containsKey(key: K): Boolean = map.containsKey(key)
    override fun containsValue(value: V): Boolean = map.containsValue(SoftReference(value))
    override fun get(key: K): V? = map[key]?.get()
    override fun isEmpty(): Boolean = map.isEmpty()
    override fun putAll(from: Map<out K, V>) = from.forEach { (k, u) -> map.put(k, SoftReference(u)) }
}