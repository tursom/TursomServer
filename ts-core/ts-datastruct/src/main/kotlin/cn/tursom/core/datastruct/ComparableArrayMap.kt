package cn.tursom.core.datastruct

@Suppress("MemberVisibilityCanBePrivate")
class ComparableArrayMap<K : Comparable<K>, V>(initialCapacity: Int = 16) : ArrayMap<K, V>(initialCapacity) {
  /**
   * @param key 查找的键
   * @return 键所在的下标
   * @return < 0 如果键不存在, -${return} - 1 为如果插入应插入的下标
   */
  override fun search(key: K): Int {
    if (end == 0) return -1
    return arr.binarySearch(key, 0, end)
  }

  override infix fun putAll(from: Map<out K, V>) {
    from.forEach { (key, value) ->
      val index = search(key)
      if (index < 0) arr[end++] = Node(key, value)
      else {
        val node = arr[index]
        if (node != null) node.value = value
        else arr[index] = Node(key, value)
      }
    }
    arr.sort()
  }
}
