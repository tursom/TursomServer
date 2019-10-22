package cn.tursom.socket

import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object AsyncAioClient {
  private val handler = object : CompletionHandler<Void, Continuation<Void?>> {
    override fun completed(result: Void?, attachment: Continuation<Void?>) {
      attachment.resume(result)
    }

    override fun failed(exc: Throwable, attachment: Continuation<Void?>) {
      attachment.resumeWithException(exc)
    }
  }

  suspend fun connect(host: String, port: Int): AsyncAioSocket {
    @Suppress("BlockingMethodInNonBlockingContext")
    return connect(AsynchronousSocketChannel.open()!!, host, port)
  }

  suspend fun connect(socketChannel: AsynchronousSocketChannel, host: String, port: Int): AsyncAioSocket {
    suspendCoroutine<Void?> { cont -> socketChannel.connect(InetSocketAddress(host, port) as SocketAddress, cont, handler) }
    return AsyncAioSocket(socketChannel)
  }
}