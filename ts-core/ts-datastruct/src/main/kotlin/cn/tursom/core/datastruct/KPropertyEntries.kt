package cn.tursom.core.datastruct

import kotlin.reflect.KProperty1

class KPropertyEntries(
  val target: Any,
  private val propertyMap: Map<String, KProperty1<Any, *>> = KPropertyValueMap[target]
) : Set<Map.Entry<String, Any?>> {
  override val size: Int = propertyMap.size
  override fun isEmpty(): Boolean = propertyMap.isEmpty()
  override fun iterator(): Iterator<Map.Entry<String, Any?>> = KPropertyEntriesIterator(target, propertyMap)
  override fun contains(element: Map.Entry<String, Any?>): Boolean =
    propertyMap[element.key]?.get(target) == element.value

  override fun containsAll(elements: Collection<Map.Entry<String, Any?>>): Boolean {
    elements.forEach { if (!contains(it)) return false }
    return true
  }
}