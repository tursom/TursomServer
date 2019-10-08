package cn.tursom.socket

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object AsyncClient {
	private val handler = object : CompletionHandler<Void, Continuation<Void?>> {
		override fun completed(result: Void?, attachment: Continuation<Void?>) {
			GlobalScope.launch {
				attachment.resume(result)
			}
		}
		
		override fun failed(exc: Throwable, attachment: Continuation<Void?>) {
			GlobalScope.launch {
				attachment.resumeWithException(exc)
			}
		}
	}
	
	suspend fun connect(host: String, port: Int): AsyncAioSocket {
		@Suppress("BlockingMethodInNonBlockingContext")
		return connect(AsynchronousSocketChannel.open()!!, host, port)
	}
	
	suspend fun connect(socketChannel: AsynchronousSocketChannel, host: String, port: Int): AsyncAioSocket {
		suspendCoroutine<Void?> { cont ->
			socketChannel.connect(InetSocketAddress(host, port) as SocketAddress, cont,
                handler
            )
		}
		return AsyncAioSocket(socketChannel)
	}
	
	suspend fun connectCached(host: String, port: Int): AsyncAioSocket {
		@Suppress("BlockingMethodInNonBlockingContext")
		return connectCached(AsynchronousSocketChannel.open()!!, host, port)
	}
	
	suspend fun connectCached(socketChannel: AsynchronousSocketChannel, host: String, port: Int): AsyncAioSocket {
		suspendCoroutine<Void?> { cont ->
			socketChannel.connect(InetSocketAddress(host, port) as SocketAddress, cont,
                handler
            )
		}
		return AsyncCachedAioSocket(socketChannel)
	}
}