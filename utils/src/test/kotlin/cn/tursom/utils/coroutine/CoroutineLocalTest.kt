package cn.tursom.utils.coroutine

import cn.tursom.core.usingTime
import kotlinx.coroutines.delay

val testCoroutineLocal = CoroutineLocal<Int>()
val testCoroutineLocalList = Array(100000) {
  CoroutineLocal<Int>()
}.asList()

fun main() = runBlockingWithCoroutineLocalContext {
  println(coroutineContext)
  launchWithCoroutineLocalAndCoroutineScopeContext {
    println(coroutineContext)
    println(CoroutineScopeContext.get())
    testCoroutineLocal set 0
    testCoroutineLocal set 0
    testCoroutineLocalList.forEachIndexed { index, coroutineLocal ->
      coroutineLocal set index
    }
    println(usingTime {
      repeat(10000000) {
        testCoroutineLocal.get()
      }
    })
  }.join()
  delay(1000)
  println(CoroutineLocal)
}