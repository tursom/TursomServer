package cn.tursom.utils.coroutine

import cn.tursom.core.cast
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext


fun CoroutineScope.launchWithCoroutineLocalContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> Unit
): Job {
  return launch(context + CoroutineLocalContext(map), start, block)
}

@Suppress("DeferredIsResult")
fun <T> CoroutineScope.asyncWithCoroutineLocalContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> T
): Deferred<T> {
  return async(context + CoroutineLocalContext(map), start, block)
}

suspend fun <T> withCoroutineLocalContext(
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> T
): T {
  return withContext(CoroutineLocalContext(map), block)
}

@Throws(InterruptedException::class)
fun <T> runBlockingWithCoroutineLocalContext(
  context: CoroutineContext = EmptyCoroutineContext,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> T
): T {
  return runBlocking(context + CoroutineLocalContext(map), block)
}

fun CoroutineScope.launchWithCoroutineScopeContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): Job {
  return launch(context + CoroutineScopeContext(this), start, block)
}

@Suppress("DeferredIsResult")
fun <T> CoroutineScope.asyncWithCoroutineScopeContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> T
): Deferred<T> {
  return async(context + CoroutineScopeContext(this), start, block)
}

fun CoroutineScope.launchWithCoroutineLocalAndCoroutineScopeContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> Unit
): Job {
  return launch(context + CoroutineLocalContext(map) + CoroutineScopeContext(this), start, block)
}

@Suppress("DeferredIsResult")
fun <T> CoroutineScope.asyncWithCoroutineLocalAndCoroutineScopeContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> T
): Deferred<T> {
  return async(context + CoroutineLocalContext(map) + CoroutineScopeContext(this), start, block)
}

@Throws(InterruptedException::class)
fun <T> runBlockingWithCoroutineLocalAndCoroutineScopeContext(
  context: CoroutineContext = EmptyCoroutineContext,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> T
): T {
  return runBlocking(context + CoroutineLocalContext(map) + CoroutineScopeContext()) {
    CoroutineScopeContext set this
    block()
  }
}


fun CoroutineScope.launchWithEnhanceContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> Unit
): Job {
  return launch(context + CoroutineLocalContext(map), start, block)
}

@Suppress("DeferredIsResult")
fun <T> CoroutineScope.asyncWithEnhanceContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> T
): Deferred<T> {
  return async(context + CoroutineLocalContext(map), start, block)
}

@Throws(InterruptedException::class)
fun <T> runBlockingWithEnhanceContext(
  context: CoroutineContext = EmptyCoroutineContext,
  map: MutableMap<CoroutineLocal<*>, Any?> = HashMap(4),
  block: suspend CoroutineScope.() -> T
): T {
  return runBlocking(context + CoroutineLocalContext(map)) {
    block()
  }
}

suspend inline fun <T> runWithCoroutineLocalContext(block: () -> T): T {
  return (block.cast<(Continuation<*>) -> T>()).invoke(CoroutineLocalContinuation(getContinuation()))
}

suspend inline fun <T> runWithCoroutineLocal(block: () -> T): T {
  if (coroutineContext[CoroutineLocalContext] == null) {
    return runWithCoroutineLocalContext(block)
  }
  return block()
}

@Suppress("NOTHING_TO_INLINE")
inline fun getContinuation(continuation: Continuation<*>): Continuation<*> {
  return continuation
}

suspend inline fun getContinuation(): Continuation<*> {
  val getContinuation: (continuation: Continuation<*>) -> Continuation<*> = ::getContinuation
  return (getContinuation.cast<suspend () -> Continuation<*>>()).invoke()
}