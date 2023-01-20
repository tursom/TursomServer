package cn.tursom.core.context

import java.util.concurrent.atomic.AtomicInteger

interface ContextEnv {
  companion object {
    private val contextEnvIdGenerator = AtomicInteger()
    fun newEnvId() = contextEnvIdGenerator.incrementAndGet()
  }

  val envId: Int

  fun emptyContext(): Context = newContext()
  fun newContext(): Context
  fun <T> newKey(): ContextKey<T>
}
