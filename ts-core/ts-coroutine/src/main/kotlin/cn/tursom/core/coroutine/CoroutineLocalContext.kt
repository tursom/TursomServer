package cn.tursom.core.coroutine

import cn.tursom.core.util.cast
import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.CoroutineContext

open class CoroutineLocalContext(
  private val mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
) : CoroutineContext.Element, ThreadContextElement<MutableMap<CoroutineLocal<*>, Any?>>,
  MutableMap<CoroutineLocal<*>, Any?> {
  override val key: CoroutineContext.Key<*> get() = Key
  private var map: MutableMap<CoroutineLocal<*>, Any?> = mapBuilder()

  override fun toString(): String {
    return "CoroutineLocalContext$map"
  }

  companion object Key : CoroutineContext.Key<CoroutineLocalContext>

  override fun restoreThreadContext(context: CoroutineContext, oldState: MutableMap<CoroutineLocal<*>, Any?>) {
    map = oldState
  }

  override fun updateThreadContext(context: CoroutineContext): MutableMap<CoroutineLocal<*>, Any?> {
    val oldState = map
    map = mapBuilder()
    return oldState
  }

  @JvmName("getTyped")
  operator fun <T> get(key: CoroutineLocal<T>): T? = map[key].cast()
  operator fun <T> set(key: CoroutineLocal<T>, value: T?): T? = map.put(key, value).cast()

  override val size: Int get() = map.size
  override fun containsKey(key: CoroutineLocal<*>): Boolean = map.containsKey(key)
  override fun containsValue(value: Any?): Boolean = map.containsValue(value)
  override fun get(key: CoroutineLocal<*>): Any? = map[key]
  override fun isEmpty(): Boolean = map.isEmpty()
  override val entries: MutableSet<MutableMap.MutableEntry<CoroutineLocal<*>, Any?>> get() = map.entries
  override val keys: MutableSet<CoroutineLocal<*>> get() = map.keys
  override val values: MutableCollection<Any?> get() = map.values
  override fun clear() = map.clear()
  override fun put(key: CoroutineLocal<*>, value: Any?): Any? = map.put(key, value)
  override fun putAll(from: Map<out CoroutineLocal<*>, Any?>) = map.putAll(from)
  override fun remove(key: CoroutineLocal<*>): Any? = map.remove(key)
}