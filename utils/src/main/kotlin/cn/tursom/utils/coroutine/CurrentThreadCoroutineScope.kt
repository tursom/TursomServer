package cn.tursom.utils.coroutine

import cn.tursom.core.cast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

object CurrentThreadCoroutineScope {
  private val currentThreadCoroutineScopeThreadLocal = ThreadLocal<CoroutineScope>()

  private suspend fun getCoroutineScope(): CoroutineScope {
    return currentThreadCoroutineScopeThreadLocal.get() ?: kotlin.run {
      val eventLoop = newBlockingEventLoop()
      val coroutineScope = newBlockingCoroutine(coroutineContext, Thread.currentThread(), eventLoop)
      currentThreadCoroutineScopeThreadLocal.set(coroutineScope)
      coroutineScope
    }
  }

  suspend fun launch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ) {
    getCoroutineScope().start(start, block = block)
  }

  private val BlockingEventLoop = Class.forName("kotlinx.coroutines.BlockingEventLoop")
  private val BlockingEventLoopConstructor = BlockingEventLoop
    .getConstructor(Thread::class.java)
    .apply { isAccessible = true }

  private fun newBlockingEventLoop(thread: Thread = Thread.currentThread()): CoroutineDispatcher {
    return BlockingEventLoopConstructor.newInstance(thread) as CoroutineDispatcher
  }

  private val BlockingCoroutine = Class.forName("kotlinx.coroutines.BlockingCoroutine")
  private val BlockingCoroutineConstructor = BlockingCoroutine.constructors[0].apply { isAccessible = true }
  private val BlockingCoroutineStart = BlockingCoroutine.methods.first { it.name == "start" && it.parameters.size == 3 }.apply { isAccessible = true }
  private val BlockingCoroutineJoinBlocking = BlockingCoroutine.methods.first { it.name == "joinBlocking" }.apply { isAccessible = true }
  //private val BlockingCoroutineOnCompleted = BlockingCoroutine.methods.first { it.name == "onCompleted" }.apply { isAccessible = true }

  private fun newBlockingCoroutine(
    coroutineContext: CoroutineContext,
    thread: Thread = Thread.currentThread(),
    eventLoop: CoroutineDispatcher
  ): CoroutineScope {
    return BlockingCoroutineConstructor.newInstance(coroutineContext, thread, eventLoop).cast()
  }

  private fun <T> CoroutineScope.start(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    receiver: CoroutineScope = this,
    block: suspend CoroutineScope.() -> T
  ) {
    BlockingCoroutineStart.invoke(this, start, receiver, block)
  }

  private fun <T> CoroutineScope.joinBlocking(): T {
    return BlockingCoroutineJoinBlocking.invoke(this).cast()
  }
}