package cn.tursom.core.datastruct

import cn.tursom.core.cast
import java.util.NoSuchElementException

class DefaultValueMap<K, V>(
  private val map: Map<K, V?>,
  private val defaultValue: (K) -> V
) : Map<K, V> by map.cast() {
  override val entries: Set<Map.Entry<K, V>> get() = Entry()
  override val values: Collection<V> get() = Values()

  override fun get(key: K): V {
    val value = map[key]
    return value ?: run {
      val newValue = defaultValue(key)
      if (map is MutableMap) {
        map[key] = newValue
      }
      newValue
    }
  }

  inner class Entry(
    private val entries: Set<Map.Entry<K, V?>> = map.entries
  ) : Set<Map.Entry<K, V>> by entries.cast() {
    override val size: Int get() = entries.count { it.value != null }
    override fun isEmpty(): Boolean = entries.firstOrNull { it.value != null } == null
    override fun iterator(): Iterator<Map.Entry<K, V>> = EntryIterator()

    inner class EntryIterator : Iterator<Map.Entry<K, V>> {
      private val iterator = entries.iterator()
      private var next: Map.Entry<K, V>? = null
      override fun hasNext(): Boolean {
        while (iterator.hasNext()) {
          next = iterator.next().cast()
          if (next?.value != null) {
            return true
          }
        }
        return false
      }

      override fun next(): Map.Entry<K, V> = when {
        next != null -> next
        hasNext() -> next
        else -> throw NoSuchElementException()
      }.cast()
    }
  }

  inner class Values(
    private val values: Collection<V?> = map.values
  ) : Collection<V> by values.cast() {
    override val size: Int get() = values.count { it != null }
    override fun isEmpty(): Boolean = values.first { it != null } == null
    override fun iterator(): Iterator<V> = ValuesIterator()

    inner class ValuesIterator : Iterator<V> {
      private val iterator = values.iterator()
      private var next: V? = null

      override fun hasNext(): Boolean {
        while (iterator.hasNext()) {
          next = iterator.next().cast()
          if (next != null) {
            return true
          }
        }
        return false
      }

      override fun next(): V = when {
        next != null -> next
        hasNext() -> next
        else -> throw NoSuchElementException()
      }.cast()
    }
  }
}