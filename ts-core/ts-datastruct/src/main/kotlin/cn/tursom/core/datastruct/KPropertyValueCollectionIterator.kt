package cn.tursom.core.datastruct

import kotlin.reflect.KProperty1

class KPropertyValueCollectionIterator(val target: Any, propertyMap: Map<String, KProperty1<Any, *>>) : Iterator<Any?> {
  private val iterator = propertyMap.iterator()
  override fun hasNext(): Boolean = iterator.hasNext()
  override fun next(): Any? = iterator.next().value.get(target)
}