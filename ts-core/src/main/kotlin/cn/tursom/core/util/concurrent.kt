package cn.tursom.core.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

inline fun <V> withFuture(callback: (future: CompletableFuture<V>) -> Unit): V {
  val future = CompletableFuture<V>()
  callback(future)
  return future.get()
}

inline fun <V> withFuture(
  timeout: Long,
  timeUnit: TimeUnit,
  callback: (future: CompletableFuture<V>) -> Unit,
): V {
  val future = CompletableFuture<V>()
  callback(future)
  return future.get(timeout, timeUnit)
}

fun <R> withTimeout(
  timeout: Long,
  timeUnit: TimeUnit,
  callback: () -> R,
): R {
  val future = CompletableFuture<R>()

  val execThread = Thread.ofVirtual()
    .start {
      future.complete(callback())
    }

  try {
    return future.get(timeout, timeUnit)
  } catch (t: Throwable) {
    execThread.interrupt()
    future.cancel(true)

    throw t
  }
}
