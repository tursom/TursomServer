package cn.tursom.utils.coroutine

import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

val testCoroutineLocal = CoroutineLocal<Int>()

suspend fun testCustomContext() {
  testCoroutineLocal.set(1)
  testInlineCustomContext()
}

suspend fun testInlineCustomContext() {
  println(coroutineContext)
  println("===================")
}

annotation class Request(val url: String, val method: String = "GET")

interface CoroutineLocalTest {
  @Request("http://tursom.cn:15015/living")
  suspend fun test(): String
}

class Test : CoroutineScope by MainScope() {
  suspend fun test() {
    testCoroutineLocal.set(1)
    println(coroutineContext)
    coroutineScope {
      println(coroutineContext)
      delay(1)
    }
  }
}

suspend fun main() {
  //MainDispatcher.init()
  runOnUiThread {
    Test().test()
    println(testCoroutineLocal.get())
    println(coroutineContext)
    GlobalScope.launch(Dispatchers.Main) {
      println(coroutineContext)
    }
    //runOnUiThread {
    //  println(coroutineContext)
    //  println(testCoroutineLocal.get())
    //}
  }
  MainDispatcher.close()
}