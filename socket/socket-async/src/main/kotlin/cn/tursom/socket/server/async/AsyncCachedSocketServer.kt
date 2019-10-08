package cn.tursom.socket.server.async

import cn.tursom.socket.AsyncCachedAioSocket
import cn.tursom.socket.useCachedNonBlock
import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousCloseException
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler

class AsyncCachedSocketServer(
	port: Int,
	host: String = "0.0.0.0",
	private val handler: suspend AsyncCachedAioSocket.() -> Unit
) : Runnable, Closeable {
	private val server = AsynchronousServerSocketChannel
		.open()
		.bind(InetSocketAddress(host, port))
	
	
	override fun run() {
		server.accept(0, object : CompletionHandler<AsynchronousSocketChannel, Int> {
			override fun completed(result: AsynchronousSocketChannel?, attachment: Int) {
				try {
					server.accept(attachment + 1, this)
				} catch (e: Throwable) {
					e.printStackTrace()
				}
				AsyncCachedAioSocket(result ?: return) useCachedNonBlock {
					handler()
				}
			}
			
			override fun failed(exc: Throwable?, attachment: Int?) {
				when (exc) {
					is AsynchronousCloseException -> {
					}
					else -> exc?.printStackTrace()
				}
			}
		})
	}
	
	override fun close() {
		server.close()
	}
}