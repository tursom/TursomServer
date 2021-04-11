package cn.tursom.core.coroutine

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.internal.MainDispatcherFactory

@Suppress("unused")
@InternalCoroutinesApi
class MainCoroutineDispatcherFactory : MainDispatcherFactory {
  override val loadPriority: Int = 1

  override fun createDispatcher(allFactories: List<MainDispatcherFactory>): MainCoroutineDispatcher = MainDispatcher
}