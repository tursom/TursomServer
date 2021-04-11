package cn.tursom.niothread

import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun NioThread.registerSuspend(channel: SelectableChannel, ops: Int): SelectionKey {
  return suspendCoroutine { continuation ->
    register(channel, ops) {
      continuation.resume(it)
    }
  }
}