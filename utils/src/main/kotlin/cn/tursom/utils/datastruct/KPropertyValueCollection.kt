package cn.tursom.utils.datastruct

import cn.tursom.core.datastruct.KPropertyValueCollectionIterator
import kotlin.reflect.KProperty1

class KPropertyValueCollection(val target: Any, private val propertyMap: Map<String, KProperty1<Any, *>> = KPropertyValueMap[target]) : Collection<Any?> {
    override val size: Int get() = propertyMap.size
    override fun isEmpty(): Boolean = propertyMap.isEmpty()
    override fun iterator(): Iterator<Any?> = KPropertyValueCollectionIterator(target, propertyMap)

    override fun contains(element: Any?): Boolean {
        propertyMap.forEach { (_, u) -> if (u.get(target) == element) return true }
        return false
    }

    override fun containsAll(elements: Collection<Any?>): Boolean {
        val mutableElements = elements.toMutableSet()
        propertyMap.forEach { (_, u) -> mutableElements.remove(u.get(target)) }
        return mutableElements.isEmpty()
    }
}