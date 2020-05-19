package cn.tursom.web

import cn.tursom.core.buffer.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun HttpContent.getBody(): ByteBuffer {
  suspendCoroutine<Boolean> { cont ->
    waitBody {
      cont.resume(it)
    }
  }
  return body!!
}

suspend fun HttpContent.waitBodyParam(): HttpContent {
  suspendCoroutine<Boolean> { cont ->
    waitBody {
      addBodyParam()
      cont.resume(it)
    }
  }
  return this
}