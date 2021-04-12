package cn.tursom.core.datastruct

import cn.tursom.core.uncheckedCast

@Suppress("MemberVisibilityCanBePrivate")
open class ParallelArrayMap<K, V>(initialCapacity: Int = 16) : SimpMap<K, V> {
  @Volatile
  protected var arr: Array<Any?> = Array(initialCapacity) { null }

  @Volatile
  protected var arrValue: Array<Any?> = Array(initialCapacity) { null }

  @Volatile
  protected var end = 0

  override val size: Int get() = end

  @Suppress("LeakingThis")
  override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = EntrySet(this)
  override val keys: MutableSet<K> get() = arrValue.asList().subList(0, end).toMutableSet().uncheckedCast()
  override val values: MutableCollection<V> get() = arrValue.asList().uncheckedCast<MutableList<V>>().subList(0, end)

  /**
   * @param key 查找的键
   * @return 键所在的下标
   * @return < 0 如果键不存在, -${return} - 1 为如果插入应插入的下标
   */
  open fun search(key: K): Int {
    if (end == 0) return -1
    for (index in 0 until end) {
      if ((arr[index] ?: continue) == key) {
        return index
      }
    }
    return -1
  }

  private fun resize(newSize: Int = if (arr.isNotEmpty()) arr.size * 2 else 1) {
    arr = arr.copyOf(newSize)
    arrValue = arrValue.copyOf(newSize)
  }

  override fun setAndGet(key: K, value: V): V? {
    var index = search(key)
    if (index < 0 || index >= end) {
      if (end == arr.size) {
        resize()
      }
      index = end++
      arr[index] = key
    }
    return setByIndex(index, value)
  }

  override fun set(key: K, value: V) {
    setAndGet(key, value)
  }

  fun setByIndex(index: Int, value: V): V? {
    val oldValue = arrValue[end]
    arrValue[index] = value
    return oldValue.uncheckedCast()
  }

  override fun delete(key: K): V? {
    return deleteByIndex(search(key))
  }

  fun deleteByIndex(index: Int): V? {
    if (index < 0 || index >= end) return null
    val oldValue = arrValue[index]
    System.arraycopy(arr, index + 1, arr, index, end - index - 1)
    System.arraycopy(arrValue, index + 1, arrValue, index, end - index - 1)
    end--
    return oldValue.uncheckedCast()
  }

  override fun clear() {
    end = 0
  }

  override fun first(): V? {
    return if (end <= 0) {
      null
    } else {
      arrValue[0].uncheckedCast()
    }
  }

  override fun containsKey(key: K): Boolean {
    return search(key) >= 0
  }

  override fun containsValue(value: V): Boolean {
    if (end == 0) return false
    for (index in 0 until end) {
      if ((arrValue[index] ?: continue) == value) {
        return true
      }
    }
    return false
  }

  override fun get(key: K): V? {
    return getByIndex(search(key))
  }

  fun getKeyByIndex(index: Int): K? {
    return if (index < 0 || index >= end) {
      null
    } else {
      arr[index].uncheckedCast()
    }
  }

  fun getByIndex(index: Int): V? {
    return if (index < 0 || index >= end) {
      null
    } else {
      arrValue[index].uncheckedCast()
    }
  }

  override fun isEmpty(): Boolean = end <= 0

  class EntrySet<K, V>(private val map: ParallelArrayMap<K, V>) : MutableSet<MutableMap.MutableEntry<K, V>> {
    override val size: Int get() = map.size
    override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean = map[element.key] == element.value
    override fun isEmpty(): Boolean = map.isEmpty()
    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = MapIterator(map)
    override fun clear() = map.clear()

    override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
      elements.forEach {
        if (!contains(it)) return false
      }
      return true
    }

    override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
      map[element.key] = element.value
      return true
    }

    override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
      elements.forEach { add(it) }
      return true
    }

    override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
      val index = map.search(element.key)
      val value = map.getByIndex(index)
      return if (value == element.value) {
        map.deleteByIndex(index)
        true
      } else {
        false
      }
    }

    override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
      elements.forEach { remove(it) }
      return true
    }

    override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
      val contains = elements.filter { map[it.key] == it.value }
      map.clear()
      map.resize(contains.size)
      addAll(contains)
      return true
    }
  }

  class MapIterator<K, V>(private val map: ParallelArrayMap<K, V>) : MutableIterator<MutableMap.MutableEntry<K, V>> {
    private var index = 0

    override fun hasNext(): Boolean {
      @Suppress("ControlFlowWithEmptyBody")
      while (++index < map.arr.size && index < map.end && map.arr[index] == null);
      index--
      return index < map.end
    }

    override fun next(): MutableMap.MutableEntry<K, V> = ParallelArrayMapEntry(map, index++)

    override fun remove() {
      map.deleteByIndex(index)
      index--
    }
  }

  class ParallelArrayMapEntry<K, V>(
    val map: ParallelArrayMap<K, V>,
    val index: Int
  ) : MutableMap.MutableEntry<K, V> {
    override val key: K get() = map.getKeyByIndex(index).uncheckedCast()
    override val value: V get() = map.getByIndex(index).uncheckedCast()
    override fun setValue(newValue: V): V {
      return map.setByIndex(index, newValue).uncheckedCast()
    }
  }
}
