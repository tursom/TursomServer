package cn.tursom.utils.coroutine

import kotlinx.coroutines.*
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

suspend fun main() {
  CurrentThreadCoroutineScope.launch {
    println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
    delay(50)
    println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
    delay(50)
    println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
  }
  GlobalScope.launch(Dispatchers.Unconfined) { // 非受限的——将和主线程一起工作
    println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
    delay(50)
    println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
    delay(50)
    println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
  }
  GlobalScope.launch { // 父协程的上下文，主 runBlocking 协程
    println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
    delay(100)
    println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
    delay(100)
    println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
  }
  println("end")
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