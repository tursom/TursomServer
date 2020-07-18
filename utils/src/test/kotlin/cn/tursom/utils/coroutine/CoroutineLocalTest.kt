package cn.tursom.utils.coroutine

import cn.tursom.core.cast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Closeable
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

fun main() {
  MainDispatcher.init()
  GlobalScope.launch(Dispatchers.Main) {
    println(Thread.currentThread().name)
  }.invokeOnCompletion {
    Dispatchers.Main.cast<Closeable>().close()
  }
}