package cn.tursom.core.datastruct

import kotlin.reflect.KProperty1

class KPropertyEntriesIterator(val target: Any, propertyMap: Map<String, KProperty1<Any, *>>) : Iterator<Map.Entry<String, Any?>> {
    private val iterator = propertyMap.iterator()
    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): Map.Entry<String, Any?> {
        val entry = iterator.next()
        return object : Map.Entry<String, Any?> {
            override val key: String = entry.key
            override val value: Any? get() = entry.value.get(target)
        }
    }
}