package cn.tursom.utils.coroutine

import kotlinx.coroutines.delay

val testCoroutineLocal = CoroutineLocal<Int>()
val testCoroutineLocalList = Array(100000) {
  CoroutineLocal<Int>()
}.asList()

fun main() = runBlockingWithCoroutineLocalAndCoroutineScopeContext {
  println(coroutineContext)
  CoroutineScopeContext.get().launchWithCoroutineLocalAndCoroutineScopeContext {
    println(coroutineContext)
    println(CoroutineScopeContext.get())
    println(Thread.currentThread().name)
    CoroutineScopeContext.get().launchWithCoroutineLocalAndCoroutineScopeContext {
      println(coroutineContext)
      println(CoroutineScopeContext.get())
      println(Thread.currentThread().name)
    }
  }.join()
  delay(1000)
  println(CoroutineLocal)
}