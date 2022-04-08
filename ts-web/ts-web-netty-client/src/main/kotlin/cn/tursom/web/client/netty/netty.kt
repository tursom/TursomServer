package cn.tursom.web.client.netty

import io.netty.util.concurrent.Future
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Future<T>.awaitSuspend() = suspendCoroutine<T> { cont ->
  addListener {
    if (isSuccess) {
      cont.resume(now)
    } else {
      cont.resumeWithException(cause())
    }
  }
}

suspend fun <T> Future<T>.awaitCancelable() = suspendCancellableCoroutine<T> { cont ->
  addListener {
    if (isSuccess) {
      cont.resume(now)
    } else {
      cont.resumeWithException(cause())
    }
  }
}
