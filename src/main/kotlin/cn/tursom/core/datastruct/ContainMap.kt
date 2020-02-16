package cn.tursom.core.datastruct

class ContainMap<K>(override val keys: Set<K>) : Map<K, Boolean> {
    override val entries: Set<Map.Entry<K, Boolean>> = EntrySet(keys)
    override val size: Int get() = keys.size
    override val values: Collection<Boolean> = listOf(true)
    override fun containsKey(key: K): Boolean = keys.contains(key)
    override fun containsValue(value: Boolean): Boolean = true
    override fun get(key: K): Boolean = keys.contains(key)
    override fun isEmpty(): Boolean = keys.isEmpty()

    private class EntrySet<K>(val keys: Set<K>) : Set<Map.Entry<K, Boolean>> {
        override val size: Int get() = keys.size
        override fun isEmpty(): Boolean = keys.isEmpty()
        override fun iterator(): Iterator<Map.Entry<K, Boolean>> = EntrySerIterator(keys)
        override fun contains(element: Map.Entry<K, Boolean>): Boolean = keys.contains(element.key) == element.value
        override fun containsAll(elements: Collection<Map.Entry<K, Boolean>>): Boolean {
            elements.forEach { if (contains(it).not()) return false }
            return true
        }
    }

    private class EntrySerIterator<K>(keys: Set<K>) : Iterator<Map.Entry<K, Boolean>> {
        private val iterator = keys.iterator()
        override fun hasNext(): Boolean = iterator.hasNext()
        override fun next(): Map.Entry<K, Boolean> = Entry(iterator.next(), true)
    }

    private class Entry<K>(override val key: K, override val value: Boolean) : Map.Entry<K, Boolean>
}