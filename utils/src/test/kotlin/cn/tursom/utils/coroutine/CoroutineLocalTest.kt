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
  suspend fun test(): Job {
    println(this)
    println(coroutineContext)
    return coroutineScope {
      println(this)
      println(coroutineContext)
      println(Thread.currentThread().name)
      delay(1)
      return@coroutineScope launch {
        println(Thread.currentThread().name)
      }
    }
  }
}

suspend fun main() {
  MainDispatcher.init()
  Test().test().join()
  MainDispatcher.close()
}