package cn.tursom.utils.coroutine

import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

val testCoroutineLocal = CoroutineLocal<Int>()

suspend fun testCustomContext() {
  testCoroutineLocal.set(1)
  testInlineCustomContext()
}

suspend fun testInlineCustomContext() {
  println(coroutineContext)
  println("===================")
}

suspend fun main() {
  println(getContinuation())
  suspendCoroutine<Int> { cont ->
    println(cont)
    cont.resume(0)
  }
  testCustomContext()
  println(testCoroutineLocal.get())
  testInlineCustomContext()
}