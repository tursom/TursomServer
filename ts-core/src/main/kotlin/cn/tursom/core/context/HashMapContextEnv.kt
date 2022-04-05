package cn.tursom.core.context

import cn.tursom.core.uncheckedCast
import java.util.concurrent.atomic.AtomicInteger

class HashMapContextEnv : ContextEnv {
  val envId = ContextEnv.newEnvId()
  private val idGenerator = AtomicInteger()

  override fun newContext(): Context = HashMapContext(envId)
  override fun <T> newKey() = ContextKey<T>(envId, idGenerator.getAndIncrement())

  private class HashMapContext(
    override val envId: Int,
  ) : Context {
    private var map = HashMap<Int, Any?>()

    override operator fun <T> get(key: ContextKey<T>): T? {
      checkEnv(key)
      return map[key.id]?.uncheckedCast<T>()
    }

    override operator fun <T> set(key: ContextKey<T>, value: T) {
      checkEnv(key)
      map[key.id] = value
    }
  }
}
