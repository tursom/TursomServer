package cn.tursom.core.datastruct

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class ReadWriteMap<K, V>(val map: MutableMap<K, V>) : MutableMap<K, V> {
    private val lock = ReentrantReadWriteLock()

    override val size: Int get() = lock.read { map.size }
    override val entries get() = lock.read { map.entries }
    override val keys get() = lock.read { map.keys }
    override val values get() = lock.read { map.values }
    override fun containsKey(key: K): Boolean = lock.read { map.containsKey(key) }
    override fun containsValue(value: V): Boolean = lock.read { map.containsValue(value) }
    override fun get(key: K): V? = lock.read { map.get(key) }
    override fun isEmpty(): Boolean = lock.read { map.isEmpty() }
    override fun clear() = lock.write { map.clear() }
    override fun put(key: K, value: V): V? = lock.write { map.put(key, value) }
    override fun putAll(from: Map<out K, V>) = lock.write { map.putAll(from) }
    override fun remove(key: K): V? = lock.write { map.remove(key) }
}