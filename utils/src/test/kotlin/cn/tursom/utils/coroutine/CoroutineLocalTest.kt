package cn.tursom.utils.coroutine

import cn.tursom.core.allFields
import cn.tursom.core.cast
import kotlin.coroutines.Continuation
import kotlin.coroutines.coroutineContext

val testCoroutineLocal = CoroutineLocal<Int>()

suspend fun testCustomContext() {
  testCoroutineLocal.set(1)
  testInlineCustomContext()
}

fun Any.printMsg() {
  javaClass.allFields.forEach {
    it.isAccessible = true
    println("${it.type} ${it.name} = ${it.get(this)}")
    val value: Any? = it.get(this)
    println("${value?.javaClass} $value")
    println(it.get(this) == this)
    if (it.name == "completion") {
      println((value as Continuation<*>).context)
    }
    println()
  }
}

val BaseContinuationImpl = Class.forName("kotlin.coroutines.jvm.internal.BaseContinuationImpl")
val BaseContinuationImplCompletion = BaseContinuationImpl.getDeclaredField("completion").apply { isAccessible = true }

fun Continuation<*>.rootCompletion(): Continuation<*> {
  var completion = this.javaClass.allFields.firstOrNull { it.name == "completion" }
  val coroutineLocalContext = CoroutineLocalContext()
  @Suppress("NAME_SHADOWING") var continuation = this
  while (completion != null) {
    continuation.injectCoroutineLocalContext(coroutineLocalContext)
    completion.isAccessible = true
    val newContinuation = completion.get(continuation)?.cast<Continuation<*>>() ?: return continuation
    if (newContinuation == continuation) {
      return continuation
    }
    completion = newContinuation.javaClass.allFields.firstOrNull { it.name == "completion" }
    continuation = newContinuation
  }
  continuation.injectCoroutineLocalContext(coroutineLocalContext)
  return continuation
}

suspend inline fun testInlineCustomContext() {
  println(coroutineContext)
  println("===================")
}

suspend fun main() {
  testCustomContext()
  println(testCoroutineLocal.get())
  testInlineCustomContext()
}