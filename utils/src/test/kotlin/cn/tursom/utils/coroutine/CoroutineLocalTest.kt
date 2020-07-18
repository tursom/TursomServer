package cn.tursom.utils.coroutine

import cn.tursom.core.cast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.Continuation
import kotlin.coroutines.coroutineContext

val testCoroutineLocal = CoroutineLocal<Int>()

suspend fun test() {
  println(coroutineContext)
  println(coroutineContext[Job] is CoroutineScope)
  println(CoroutineScopeContext.get())
  println(Thread.currentThread().name)
}

@Suppress("NOTHING_TO_INLINE")
inline fun getContinuation(continuation: Continuation<*>): Continuation<*> {
  return continuation
}

suspend inline fun getContinuation(): Continuation<*> {
  val getContinuation: (continuation: Continuation<*>) -> Continuation<*> = ::getContinuation
  return (getContinuation.cast<suspend () -> Continuation<*>>()).invoke()
}

suspend fun testCustomContext(): Int? = runWithCoroutineLocal {
  println(coroutineContext)
  return testCoroutineLocal.get()
}

suspend fun main(): Unit = runWithCoroutineLocal {
  repeat(100) {
    testCoroutineLocal.set(it)
    println(testCustomContext())
  }
  ////println(::main.javaMethod?.parameters?.get(0))
  //println(coroutineContext)
  //CurrentThreadCoroutineScope.launch {
  //  println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
  //  delay(50)
  //  println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
  //  delay(50)
  //  println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
  //}
  //GlobalScope.launch(Dispatchers.Unconfined) { // 非受限的——将和主线程一起工作
  //  println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
  //  delay(50)
  //  println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
  //  delay(50)
  //  println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
  //}
  //GlobalScope.launch { // 父协程的上下文，主 runBlocking 协程
  //  println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
  //  delay(100)
  //  println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
  //  delay(100)
  //  println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
  //}
  //println("end")
  //delay(1000)
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