@file:Suppress("unused")

package cn.tursom.core.coroutine

import cn.tursom.core.cast
import cn.tursom.core.forAllFields
import cn.tursom.core.isInheritanceFrom
import cn.tursom.core.uncheckedCast
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext


fun CoroutineScope.launchWithCoroutineLocalContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
  block: suspend CoroutineScope.() -> Unit,
): Job {
  return launch(context + CoroutineLocalContext(mapBuilder), start, block)
}

@Suppress("DeferredIsResult")
fun <T> CoroutineScope.asyncWithCoroutineLocalContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
  block: suspend CoroutineScope.() -> T,
): Deferred<T> {
  return async(context + CoroutineLocalContext(mapBuilder), start, block)
}

suspend fun <T> withCoroutineLocalContext(
  mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
  block: suspend CoroutineScope.() -> T,
): T {
  return withContext(CoroutineLocalContext(mapBuilder), block)
}

@Throws(InterruptedException::class)
fun <T> runBlockingWithCoroutineLocalContext(
  context: CoroutineContext = EmptyCoroutineContext,
  mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
  block: suspend CoroutineScope.() -> T,
): T {
  return runBlocking(context + CoroutineLocalContext(mapBuilder), block)
}

fun CoroutineScope.launchWithCoroutineScopeContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit,
): Job {
  return launch(context + CoroutineScopeContext(this), start, block)
}

@Suppress("DeferredIsResult")
fun <T> CoroutineScope.asyncWithCoroutineScopeContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> T,
): Deferred<T> {
  return async(context + CoroutineScopeContext(this), start, block)
}

fun CoroutineScope.launchWithCoroutineLocalAndCoroutineScopeContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
  block: suspend CoroutineScope.() -> Unit,
): Job {
  return launch(context + CoroutineLocalContext(mapBuilder) + CoroutineScopeContext(this), start, block)
}

@Suppress("DeferredIsResult")
fun <T> CoroutineScope.asyncWithCoroutineLocalAndCoroutineScopeContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
  block: suspend CoroutineScope.() -> T,
): Deferred<T> {
  return async(context + CoroutineLocalContext(mapBuilder) + CoroutineScopeContext(this), start, block)
}

@Throws(InterruptedException::class)
fun <T> runBlockingWithCoroutineLocalAndCoroutineScopeContext(
  context: CoroutineContext = EmptyCoroutineContext,
  mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
  block: suspend CoroutineScope.() -> T,
): T {
  return runBlocking(context + CoroutineLocalContext(mapBuilder) + CoroutineScopeContext()) {
    CoroutineScopeContext set this
    block()
  }
}


fun CoroutineScope.launchWithEnhanceContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
  block: suspend CoroutineScope.() -> Unit,
): Job {
  return launch(context + CoroutineLocalContext(mapBuilder), start, block)
}

@Suppress("DeferredIsResult")
fun <T> CoroutineScope.asyncWithEnhanceContext(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
  block: suspend CoroutineScope.() -> T,
): Deferred<T> {
  return async(context + CoroutineLocalContext(mapBuilder), start, block)
}

@Throws(InterruptedException::class)
fun <T> runBlockingWithEnhanceContext(
  context: CoroutineContext = EmptyCoroutineContext,
  mapBuilder: () -> MutableMap<CoroutineLocal<*>, Any?> = { HashMap(4) },
  block: suspend CoroutineScope.() -> T,
): T {
  return runBlocking(context + CoroutineLocalContext(mapBuilder)) {
    block()
  }
}

suspend fun <T> runWithCoroutineLocalContext(
  block: suspend () -> T,
): T {
  val continuation: Any? = getContinuation()
  val coroutineLocalContinuation = if (continuation is Continuation<*>) {
    CoroutineLocalContinuation(continuation.cast())
  } else {
    return continuation.cast()
  }
  return (block.cast<(Any?) -> T>()).invoke(coroutineLocalContinuation)
}

suspend fun <T> runWithCoroutineLocal(
  block: suspend () -> T,
): T {
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

suspend inline fun <T> getTypedContinuation(): Continuation<T> = getContinuation().uncheckedCast()

suspend fun injectCoroutineContext(
  coroutineContext: CoroutineContext,
  key: CoroutineContext.Key<out CoroutineContext.Element>? = null,
): Boolean {
  return if (key == null || coroutineContext[key] == null) {
    getContinuation().injectCoroutineContext(coroutineContext, key)
  } else {
    true
  }
}

suspend fun injectCoroutineLocalContext(coroutineLocalContext: CoroutineLocalContext? = null): Boolean {
  return if (coroutineContext[CoroutineLocalContext] == null) {
    getContinuation().injectCoroutineLocalContext(coroutineLocalContext)
  } else {
    true
  }
}

fun Continuation<*>.injectCoroutineLocalContext(
  coroutineLocalContext: CoroutineLocalContext? = null,
): Boolean {
  return if (context[CoroutineLocalContext] == null) {
    injectCoroutineContext(coroutineLocalContext ?: CoroutineLocalContext(), CoroutineLocalContext)
  } else {
    true
  }
}

private val BaseContinuationImpl = Class.forName("kotlin.coroutines.jvm.internal.BaseContinuationImpl")
private val BaseContinuationImplCompletion =
  BaseContinuationImpl.getDeclaredField("completion").apply { isAccessible = true }

fun Continuation<*>.injectCoroutineContext(
  coroutineContext: CoroutineContext,
  key: CoroutineContext.Key<out CoroutineContext.Element>? = null,
): Boolean {
  if (key != null && context[key] != null) return true
  if (BaseContinuationImpl.isInstance(this)) {
    BaseContinuationImplCompletion.get(this).cast<Continuation<*>>().injectCoroutineContext(coroutineContext, key)
  }
  combinedContext(context)
  if (context[CoroutineLocalContext] != null) return true
  javaClass.forAllFields {
    if (!it.type.isInheritanceFrom(CoroutineContext::class.java)) {
      return@forAllFields
    }
    it.isAccessible = true
    it.set(this, it.get(this).cast<CoroutineContext>() + coroutineContext)
  }
  return context[CoroutineLocalContext] != null
}

private val combinedContextClass = Class.forName("kotlin.coroutines.CombinedContext")
private val left = combinedContextClass.getDeclaredField("left").apply { isAccessible = true }

fun combinedContext(coroutineContext: CoroutineContext): Boolean {
  if (!combinedContextClass.isInstance(coroutineContext)) return false
  if (coroutineContext[CoroutineLocalContext] == null) {
    val leftObj = left.get(coroutineContext).cast<CoroutineContext>()
    left.set(coroutineContext, leftObj + CoroutineLocalContext())
  }
  return true
}

//fun CoroutineScope.runOnUiThread(action: suspend CoroutineScope.() -> Unit): Job {
//  return launch(Dispatchers.Main, block = action)
//}

suspend fun <T> runOnUiThread(
  coroutineContext: CoroutineContext = EmptyCoroutineContext,
  action: suspend CoroutineScope.() -> T,
): T {
  return withContext(coroutineContext + Dispatchers.Main, action)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun bufferTicker(
  delayMillis: Long,
  capacity: Int = 16,
  waitTimeout: Long = 0,
  initialDelayMillis: Long = delayMillis,
  context: CoroutineContext = EmptyCoroutineContext,
): ReceiveChannel<Unit> {
  return GlobalScope.produce(Dispatchers.Unconfined + context, capacity = capacity) {
    if (initialDelayMillis > 0) {
      delay(initialDelayMillis)
    }
    while (true) {
      if (waitTimeout > 0) withTimeoutOrNull(waitTimeout) {
        channel.send(Unit)
      } ?: return@produce else {
        channel.send(Unit)
      }
      delay(delayMillis)
    }
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun fastSlowTicker(
  fastTicker: ReceiveChannel<Unit>,
  slowTicker: ReceiveChannel<Unit>,
  maxFailure: Int = 5,
) = GlobalScope.produce {
  var failure = 0
  while (true) {
    var receive = fastTicker.tryReceive()
    if (receive.isSuccess) {
      failure = 0
      send(Unit)
      continue
    }

    failure++
    if (failure < maxFailure) {
      send(fastTicker.receive())
      continue
    }

    send(slowTicker.receive())
    do {
      receive = fastTicker.tryReceive()
    } while (receive.isSuccess)
    failure--
  }
}
