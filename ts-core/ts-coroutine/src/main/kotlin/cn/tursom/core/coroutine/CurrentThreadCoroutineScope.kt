package cn.tursom.core.coroutine

import cn.tursom.core.util.ThreadLocal
import cn.tursom.core.util.cast
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

object CurrentThreadCoroutineScope {
  private val eventLoopThreadLocal: ThreadLocal<CoroutineDispatcher> = ThreadLocal {
    newBlockingEventLoop()
  }

  private suspend fun getCoroutineScope(): CoroutineScope {
    val eventLoop = eventLoopThreadLocal.get()
    val coroutineScopeContext = CoroutineScopeContext()
    val newBlockingCoroutine = newBlockingCoroutine(
      coroutineContext + coroutineScopeContext + Dispatchers.Unconfined,
      Thread.currentThread(),
      eventLoop
    )
    coroutineScopeContext.coroutineScope = newBlockingCoroutine
    return newBlockingCoroutine
  }

  suspend fun launch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
  ): Job {
    val coroutineScope = getCoroutineScope()
    //coroutineScope.launch(start = start, block = block)
    coroutineScope.start(start, block = block)
    return coroutineScope as Job
  }

  private val EventLoop = Class.forName("kotlinx.coroutines.EventLoop")
  private val EventLoopShouldBeProcessedFromContext = EventLoop.methods
    .first { it.name == "shouldBeProcessedFromContext" }
    .apply { isAccessible = true }

  private val BlockingEventLoop = Class.forName("kotlinx.coroutines.BlockingEventLoop")
  private val BlockingEventLoopConstructor = BlockingEventLoop
    .getConstructor(Thread::class.java)
    .apply { isAccessible = true }

  private fun newBlockingEventLoop(thread: Thread = Thread.currentThread()): CoroutineDispatcher {
    return BlockingEventLoopConstructor.newInstance(thread) as CoroutineDispatcher
  }

  private val BlockingCoroutine = Class.forName("kotlinx.coroutines.BlockingCoroutine")
  private val BlockingCoroutineConstructor = BlockingCoroutine.constructors[0].apply { isAccessible = true }
  private val BlockingCoroutineStart =
    BlockingCoroutine.methods.first { it.name == "start" && it.parameters.size == 3 }.apply { isAccessible = true }
  private val BlockingCoroutineJoinBlocking =
    BlockingCoroutine.methods.first { it.name == "joinBlocking" }.apply { isAccessible = true }
  //private val BlockingCoroutineOnCompleted = BlockingCoroutine.methods.first { it.name == "onCompleted" }.apply { isAccessible = true }

  private fun newBlockingCoroutine(
    coroutineContext: CoroutineContext,
    thread: Thread = Thread.currentThread(),
    eventLoop: CoroutineDispatcher,
  ): CoroutineScope {
    return BlockingCoroutineConstructor.newInstance(coroutineContext, thread, eventLoop).cast()
  }

  private fun <T> CoroutineScope.start(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    receiver: CoroutineScope = this,
    block: suspend CoroutineScope.() -> T,
  ) {
    BlockingCoroutineStart.invoke(this, start, receiver, block)
  }

  private fun <T> CoroutineScope.joinBlocking(): T {
    return BlockingCoroutineJoinBlocking.invoke(this).cast()
  }
}