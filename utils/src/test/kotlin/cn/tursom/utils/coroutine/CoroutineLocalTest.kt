package cn.tursom.utils.coroutine

import cn.tursom.core.usingTime
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val testCoroutineLocal = CoroutineLocal<Int>()
val testCoroutineLocalList = Array(100000) {
  CoroutineLocal<Int>()
}.asList()

suspend fun main() {
  println(testCoroutineLocal.set(1))
  GlobalScope.launch {
    coroutineScope { }
    coroutineContext
    testCoroutineLocal set 0
    testCoroutineLocal set 0
    testCoroutineLocalList.forEachIndexed { index, coroutineLocal ->
      coroutineLocal set index
    }
    println(usingTime {
      repeat(1000000000) {
        testCoroutineLocal.get()
      }
    })
  }.join()
  delay(1000)
  println(CoroutineLocal)
}