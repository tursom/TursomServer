package cn.tursom.core.datastruct

import cn.tursom.core.util.uncheckedCast

class DefaultValueMutableMap<K, V>(
  private val map: MutableMap<K, V?>,
  private val defaultValue: (K) -> V,
) : MutableMap<K, V> by map.uncheckedCast() {
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
    private val entries: MutableSet<MutableMap.MutableEntry<K, V?>> = map.entries,
  ) : MutableSet<MutableMap.MutableEntry<K, V>> by entries.uncheckedCast() {
    override val size: Int get() = entries.count { it.value != null }
    override fun isEmpty(): Boolean = entries.firstOrNull { it.value != null } == null
    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = EntryIterator()

    inner class EntryIterator(
      private val iterator: MutableIterator<MutableMap.MutableEntry<K, V?>> = entries.iterator(),
    ) : MutableIterator<MutableMap.MutableEntry<K, V>> by iterator.uncheckedCast() {
      private var next: Map.Entry<K, V>? = null
      override fun hasNext(): Boolean {
        while (iterator.hasNext()) {
          next = iterator.next().uncheckedCast()
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
      }.uncheckedCast()
    }
  }

  inner class Values(
    private val values: MutableCollection<V?> = map.values,
  ) : MutableCollection<V> by values.uncheckedCast() {
    override val size: Int get() = values.count { it != null }
    override fun isEmpty(): Boolean = values.first { it != null } == null
    override fun iterator(): MutableIterator<V> = ValuesIterator()

    inner class ValuesIterator(
      private val iterator: MutableIterator<V?> = values.iterator(),
    ) : MutableIterator<V> by iterator.uncheckedCast() {
      private var next: V? = null

      override fun hasNext(): Boolean {
        while (iterator.hasNext()) {
          next = iterator.next().uncheckedCast()
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
      }.uncheckedCast()
    }
  }
}