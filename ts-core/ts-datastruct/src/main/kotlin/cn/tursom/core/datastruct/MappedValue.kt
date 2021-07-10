package cn.tursom.core.datastruct

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

@Suppress("unused")
class MappedValue<V : Any>(val value: V) : Map<String, Any?> {
  @Suppress("UNCHECKED_CAST")
  val properties = (value::class as KClass<V>).memberProperties.associateBy { it.name }

  override val entries: Set<Map.Entry<String, Any?>> = Entries()
  override val keys: Set<String> = Keys()
  override val size: Int get() = properties.size
  override val values: Collection<Any?> = Values()

  override fun containsKey(key: String): Boolean = properties.containsKey(key)
  override fun containsValue(value: Any?): Boolean {
    properties.forEach { (_, u) ->
      if (u.get(this.value) == value)
        return true
    }
    return false
  }

  override fun get(key: String): Any? = properties[key]?.get(value)
  override fun isEmpty(): Boolean = properties.isEmpty()

  private inner class Entries : Set<Map.Entry<String, Any?>> {
    override val size: Int get() = this@MappedValue.size
    override fun isEmpty(): Boolean = this@MappedValue.isEmpty()
    override fun iterator(): Iterator<Map.Entry<String, Any?>> = MapIterator()
    override fun contains(element: Map.Entry<String, Any?>): Boolean = this@MappedValue[element.key] == element.value
    override fun containsAll(elements: Collection<Map.Entry<String, Any?>>): Boolean {
      elements.forEach {
        if (!contains(it))
          return false
      }
      return true
    }
  }

  private inner class MapIterator : Iterator<Map.Entry<String, Any?>> {
    private val iterator = properties.iterator()
    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): Map.Entry<String, Any?> = iterator.next().let { Entry(it.key, it.value.get(value)) }
  }

  private class Entry(override val key: String, override val value: Any?) : Map.Entry<String, Any?>

  private inner class Values : Collection<Any?> {
    override val size: Int get() = this@MappedValue.size
    override fun isEmpty(): Boolean = this@MappedValue.isEmpty()
    override fun iterator(): Iterator<Any?> = ValuesIterator(this@MappedValue.iterator())

    override fun contains(element: Any?): Boolean {
      properties.forEach { (_, u) -> if (u == element) return true }
      return false
    }

    override fun containsAll(elements: Collection<Any?>): Boolean {
      elements.forEach {
        if (!contains(it)) return false
      }
      return true
    }
  }

  private class ValuesIterator(val iterator: Iterator<Map.Entry<String, Any?>>) : Iterator<Any?> {
    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): Any? = iterator.next().value
  }

  private inner class Keys : Set<String> {
    override val size: Int get() = this@MappedValue.size
    override fun isEmpty(): Boolean = this@MappedValue.isEmpty()
    override fun contains(element: String): Boolean = properties.keys.contains(element)
    override fun containsAll(elements: Collection<String>): Boolean = properties.keys.containsAll(elements)
    override fun iterator(): Iterator<String> = properties.keys.iterator()
  }
}