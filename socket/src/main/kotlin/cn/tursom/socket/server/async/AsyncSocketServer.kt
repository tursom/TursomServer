package cn.tursom.socket.server.async

import cn.tursom.socket.AsyncAioSocket
import cn.tursom.socket.useNonBlock
import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousCloseException
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler

class AsyncSocketServer(
	port: Int,
	host: String = "0.0.0.0",
	private val handler: suspend AsyncAioSocket.() -> Unit
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
				AsyncAioSocket(result ?: return) useNonBlock {
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

