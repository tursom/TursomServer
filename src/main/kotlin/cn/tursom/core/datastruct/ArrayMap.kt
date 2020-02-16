package cn.tursom.core.datastruct

@Suppress("MemberVisibilityCanBePrivate")
class ArrayMap<K : Comparable<K>, V>(initialCapacity: Int = 16) : SimpMap<K, V> {
    @Volatile
    private var arr: Array<Node<K, V>?> = Array(initialCapacity) { null }
    @Volatile
    private var end = 0

    override val size: Int get() = end
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = EntrySet(this)
    override val keys: MutableSet<K> = KeySet(this)
    override val values: MutableCollection<V> = ValueCollection(this)

    /**
     * @param key 查找的键
     * @return 键所在的下标
     * @return < 0 如果键不存在, -${return} - 1 为如果插入应插入的下标
     */
    fun search(key: K): Int {
        if (end == 0) return -1
        return arr.binarySearch(key, 0, end)
    }

    infix fun getFromIndex(index: Int): V? {
        return if (index < 0) null
        else arr[index]?.value
    }

    override fun first(): V? = getFromIndex(0)

    override fun clear() {
        end = 0
    }

    override operator fun set(key: K, value: V) {
        setAndGet(key, value)
    }

    override fun setAndGet(key: K, value: V): V? {
        @Suppress("SENSELESS_COMPARISON")
        if (key == null) return null
        // 首先查找得到目标所在的下标
        val index = search(key)
        var prev: V? = null
        if (index < 0) {
            // 下标小于零表示不存在，直接插入数据
            insert(key, value, -index - 1)
        } else {
            val node = arr[index]
            if (node != null) {
                prev = node.value
                node.value = value
            } else arr[index] = Node(key, value)
        }
        return prev
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

    override infix fun delete(key: K): V? {
        val index = search(key)
        return delete(index)
    }

    override infix fun containsKey(key: K): Boolean = search(key) >= 0

    override infix fun containsValue(value: V): Boolean {
        arr.forEach {
            if (it?.value == value) return true
        }
        return false
    }

    override infix operator fun get(key: K): V? = getNode(key)?.value
    override fun isEmpty(): Boolean = end == 0

    fun copy(): ArrayMap<K, V> {
        val clone = ArrayMap<K, V>(0)
        clone.arr = Array(end) { arr[it]?.let { Node(it.key, it.value) } }
        clone.end = end
        return clone
    }

    override fun toString(): String {
        if (end == 0) return "{}"
        val sb = StringBuilder("{${arr[0]}")
        for (i in 1 until end) {
            sb.append(", ")
            sb.append(arr[i])
        }
        sb.append("}")
        return sb.toString()
    }

    private fun getNode(key: K): Node<K, V>? {
        @Suppress("SENSELESS_COMPARISON")
        if (key == null) return null
        val index = search(key)
        return if (index < 0) null
        else arr[index]
    }

    private fun resize(newSize: Int = if (arr.isNotEmpty()) arr.size * 2 else 1) {
        arr = arr.copyOf(newSize)
    }

    private fun insert(key: K, value: V, index: Int): V? {
        if (end == arr.size) resize()
        System.arraycopy(arr, index, arr, index + 1, end - index)
        arr[index] = Node(key, value)
        end++
        return value
    }

    private infix fun delete(index: Int): V? {
        if (index in 0 until end) {
            val oldNode = arr[index]
            System.arraycopy(arr, index + 1, arr, index, end - index - 1)
            end--
            return oldNode?.value
        }
        return null
    }

    class Node<K : Comparable<K>, V>(
        override val key: K,
        @Volatile override var value: V
    ) : Comparable<K>, MutableMap.MutableEntry<K, V> {
        override fun compareTo(other: K): Int = key.compareTo(other)
        override fun toString(): String = "$key=$value"
        override fun setValue(newValue: V): V = value.also { value = newValue }
    }


    class EntrySet<K : Comparable<K>, V>(private val map: ArrayMap<K, V>) : MutableSet<MutableMap.MutableEntry<K, V>> {
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
            val value = map.getFromIndex(index)
            return if (value == element.value) {
                map.delete(index)
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

    class MapIterator<K : Comparable<K>, V>(private val map: ArrayMap<K, V>) : MutableIterator<MutableMap.MutableEntry<K, V>> {
        private var index = 0

        override fun hasNext(): Boolean {
            @Suppress("ControlFlowWithEmptyBody")
            while (++index < map.arr.size && index < map.end && map.arr[index] == null);
            index--
            return index < map.end
        }

        override fun next(): MutableMap.MutableEntry<K, V> = map.arr[index++]!!

        override fun remove() {
            map.delete(index)
            index--
        }
    }

    class KeySet<K : Comparable<K>>(private val map: ArrayMap<K, *>) : MutableSet<K> {
        override val size: Int get() = map.size
        override fun contains(element: K): Boolean = map.containsKey(element)
        override fun isEmpty(): Boolean = size == 0
        override fun iterator(): MutableIterator<K> = KeyIterator(map)

        override fun containsAll(elements: Collection<K>): Boolean {
            elements.forEach {
                if (!map.containsKey(it)) return false
            }
            return true
        }

        override fun add(element: K): Boolean = false
        override fun addAll(elements: Collection<K>): Boolean = false
        override fun clear() = map.clear()
        override fun retainAll(elements: Collection<K>): Boolean = map.entries.retainAll(elements.map { map.getNode(it) }.filterNotNull())
        override fun remove(element: K): Boolean = map.remove(element) != null
        override fun removeAll(elements: Collection<K>): Boolean {
            elements.forEach { remove(it) }
            return true
        }
    }

    class KeyIterator<K : Comparable<K>>(map: ArrayMap<K, *>) : MutableIterator<K> {
        private val iterator = map.iterator()
        override fun hasNext(): Boolean = iterator.hasNext()
        override fun next(): K = iterator.next().key
        override fun remove() = iterator.remove()
    }

    class ValueCollection<V>(private val map: ArrayMap<*, V>) : MutableCollection<V> {
        override val size: Int get() = map.size
        override fun contains(element: V): Boolean = map.containsValue(element)
        override fun isEmpty(): Boolean = size == 0
        override fun iterator(): MutableIterator<V> = ValueIterator(map)
        override fun add(element: V): Boolean = false
        override fun addAll(elements: Collection<V>): Boolean = false
        override fun clear() = map.clear()
        override fun remove(element: V): Boolean = false
        override fun removeAll(elements: Collection<V>): Boolean = false
        override fun retainAll(elements: Collection<V>): Boolean = false

        override fun containsAll(elements: Collection<V>): Boolean {
            elements.forEach {
                if (!map.containsValue(it)) return false
            }
            return true
        }
    }

    class ValueIterator<V>(map: ArrayMap<*, V>) : MutableIterator<V> {
        private val iterator = map.iterator()
        override fun hasNext(): Boolean = iterator.hasNext()
        override fun next(): V = iterator.next().value
        override fun remove() = iterator.remove()
    }
}
