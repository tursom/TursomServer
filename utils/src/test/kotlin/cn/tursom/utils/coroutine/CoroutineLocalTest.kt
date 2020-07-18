package cn.tursom.utils.coroutine

import cn.tursom.core.cast
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

val testCoroutineLocal = CoroutineLocal<Int>()
val testCoroutineLocalList = Array(100000) {
  CoroutineLocal<Int>()
}.asList()

suspend fun test() {
  println(coroutineContext)
  println(coroutineContext[Job] is CoroutineScope)
  println(CoroutineScopeContext.get())
  println(Thread.currentThread().name)
}

val EventLoop = Class.forName("kotlinx.coroutines.EventLoop")
val AbstractCoroutine = Class.forName("kotlinx.coroutines.AbstractCoroutine")

val BlockingEventLoop = Class.forName("kotlinx.coroutines.BlockingEventLoop")
val BlockingEventLoopConstructor = BlockingEventLoop.getConstructor(Thread::class.java).apply { isAccessible = true }
fun newBlockingEventLoop(thread: Thread = Thread.currentThread()): CoroutineDispatcher {
  return BlockingEventLoopConstructor.newInstance(thread) as CoroutineDispatcher
}

val BlockingCoroutine = Class.forName("kotlinx.coroutines.BlockingCoroutine")
val BlockingCoroutineConstructor = BlockingCoroutine.constructors[0].apply { isAccessible = true }
val BlockingCoroutineStart = BlockingCoroutine.methods.first { it.name == "start" && it.parameters.size == 3 }.apply { isAccessible = true }
val BlockingCoroutineJoinBlocking = BlockingCoroutine.methods.first { it.name == "joinBlocking" }.apply { isAccessible = true }

fun newBlockingCoroutine(coroutineContext: CoroutineContext, thread: Thread = Thread.currentThread(), eventLoop: CoroutineDispatcher): CoroutineScope {
  return BlockingCoroutineConstructor.newInstance(coroutineContext, thread, eventLoop).cast()
}

fun CoroutineScope.start(start: CoroutineStart = CoroutineStart.DEFAULT, receiver: CoroutineScope = this, block: suspend CoroutineScope.() -> Any?) {
  return BlockingCoroutineStart.invoke(this, start, receiver, block).cast()
}

fun CoroutineScope.joinBlocking() {
  BlockingCoroutineJoinBlocking.invoke(this)
}

suspend fun main() {
  test()
  CurrentThreadCoroutineScope.launch {
    test()
  }
  delay(1000)
  //runBlockingWithEnhanceContext {
  //  println(coroutineContext)
  //  println(coroutineContext[Job] is CoroutineScope)
  //  println(CoroutineScopeContext.get())
  //  println(Thread.currentThread().name)
  //  CoroutineContextScope(coroutineContext).launch {
  //    println(coroutineContext)
  //    println(coroutineContext[Job] is CoroutineScope)
  //    println(CoroutineScopeContext.get())
  //    println(Thread.currentThread().name)
  //  }.join()
  //  //CoroutineScopeContext.get().launchWithEnhanceContext {
  //  //  println(coroutineContext)
  //  //  println(coroutineContext[Job] is CoroutineScope)
  //  //  println(CoroutineScopeContext.get())
  //  //  println(Thread.currentThread().name)
  //  //  CoroutineScopeContext.get().launchWithEnhanceContext {
  //  //    println(coroutineContext)
  //  //    println(coroutineContext[Job] is CoroutineScope)
  //  //    println(CoroutineScopeContext.get())
  //  //    println(Thread.currentThread().name)
  //  //  }
  //  //}.join()
  //  delay(1000)
  //  println(CoroutineLocal)
  //}
}