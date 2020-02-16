package cn.tursom.core.datastruct

interface SimpMap<K, V> : MutableMap<K, V> {
    /**
     * @return prev value
     */
    operator fun set(key: K, value: V)

    infix fun delete(key: K): V?

    fun setAndGet(key: K, value: V): V? {
        val prev = get(key)
        set(key, value)
        return prev
    }

    override fun put(key: K, value: V): V? = setAndGet(key, value)

    override fun remove(key: K): V? = delete(key)

    /**
     * 清空整个表
     */
    override fun clear()

    fun first(): V?

    override infix fun putAll(from: Map<out K, V>) {
        from.forEach { (k, u) ->
            set(k, u)
        }
    }
}

