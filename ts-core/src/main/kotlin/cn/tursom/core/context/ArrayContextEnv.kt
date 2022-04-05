package cn.tursom.core.context

import cn.tursom.core.uncheckedCast
import java.util.concurrent.atomic.AtomicInteger

class ArrayContextEnv : ContextEnv {
  val envId = ContextEnv.newEnvId()
  private val idGenerator = AtomicInteger()

  override fun newContext(): Context = ArrayContext(envId, idGenerator)
  override fun <T> newKey() = ContextKey<T>(envId, idGenerator.getAndIncrement())

  private class ArrayContext(
    override val envId: Int,
    private val idGenerator: AtomicInteger,
  ) : Context {
    private var array = arrayOfNulls<Any?>(idGenerator.get())

    override operator fun <T> get(key: ContextKey<T>): T? {
      checkEnv(key)
      return if (array.size < key.id) {
        null
      } else {
        array[key.id]?.uncheckedCast<T>()
      }
    }

    override operator fun <T> set(key: ContextKey<T>, value: T) {
      checkEnv(key)
      if (array.size < key.id) {
        array = array.copyOf(idGenerator.get())
      }
      array[key.id] = value
    }
  }
}
