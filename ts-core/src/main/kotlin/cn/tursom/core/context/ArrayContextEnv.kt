package cn.tursom.core.context

import cn.tursom.core.uncheckedCast
import java.util.concurrent.atomic.AtomicInteger

class ArrayContextEnv : ContextEnv {
  val envId = ContextEnv.newEnvId()
  private val idGenerator = AtomicInteger()
  private val emptyContext = EmptyArrayContext(envId, idGenerator)

  override fun emptyContext(): Context = emptyContext
  override fun newContext(): Context = ArrayContext(envId, idGenerator)
  override fun <T> newKey() = ContextKey<T>(envId, idGenerator.getAndIncrement())

  private class ArrayContext(
    override val envId: Int,
    private val idGenerator: AtomicInteger,
  ) : Context {
    private var array = arrayOfNulls<Any?>(idGenerator.get())
    override fun get(id: Int): Any? {
      return if (array.size > id) {
        array[id]
      } else {
        null
      }
    }

    override operator fun <T> get(key: ContextKey<T>): T? {
      checkEnv(key)
      return if (array.size > key.id) {
        array[key.id]?.uncheckedCast<T>()
      } else {
        null
      }
    }

    override fun <T> set(key: ContextKey<T>, value: T): ArrayContext {
      checkEnv(key)
      if (array.size <= key.id) {
        array = array.copyOf(idGenerator.get())
      }
      array[key.id] = value
      return this
    }
  }

  private class EmptyArrayContext(
    override val envId: Int,
    private val idGenerator: AtomicInteger,
  ) : Context {
    override fun <T> get(key: ContextKey<T>): T? = null
    override fun get(id: Int): Any? = null
    override fun <T> get(key: DefaultContextKey<T>): T = key.provider(this)
    override fun <T> set(key: ContextKey<T>, value: T) =
      ArrayContext(envId, idGenerator).set(key, value)
  }
}
