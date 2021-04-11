package cn.tursom.core.datastruct

import cn.tursom.core.cast

class DefaultValueMutableMap<K, V>(
  private val map: MutableMap<K, V?>,
  private val defaultValue: (K) -> V
) : MutableMap<K, V> by map.cast() {
  override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() = Entry()
  override val values: MutableCollection<V> get() = Values()

  override fun get(key: K): V {
    val value = map[key]
    return value ?: run {
      val newValue = defaultValue(key)
      map[key] = newValue
      newValue
    }
  }

  inner class Entry(
    private val entries: MutableSet<MutableMap.MutableEntry<K, V?>> = map.entries
  ) : MutableSet<MutableMap.MutableEntry<K, V>> by entries.cast() {
    override val size: Int get() = entries.count { it.value != null }
    override fun isEmpty(): Boolean = entries.firstOrNull { it.value != null } == null
    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = EntryIterator()

    inner class EntryIterator(
      private val iterator: MutableIterator<MutableMap.MutableEntry<K, V?>> = entries.iterator()
    ) : MutableIterator<MutableMap.MutableEntry<K, V>> by iterator.cast() {
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

      override fun next(): MutableMap.MutableEntry<K, V> = when {
        next != null -> next
        hasNext() -> next
        else -> throw NoSuchElementException()
      }.cast()
    }
  }

  inner class Values(
    private val values: MutableCollection<V?> = map.values
  ) : MutableCollection<V> by values.cast() {
    override val size: Int get() = values.count { it != null }
    override fun isEmpty(): Boolean = values.first { it != null } == null
    override fun iterator(): MutableIterator<V> = ValuesIterator()

    inner class ValuesIterator(
      private val iterator: MutableIterator<V?> = values.iterator()
    ) : MutableIterator<V> by iterator.cast() {
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