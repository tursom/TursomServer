package cn.tursom.socket.server.nio

import cn.tursom.socket.AsyncNioSocket
import cn.tursom.socket.INioProtocol
import cn.tursom.socket.niothread.INioThread
import cn.tursom.socket.server.ISocketServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.channels.SelectionKey

/**
 * 有多个工作线程的协程套接字服务器
 * 不过因为结构复杂，所以性能实际上比多线程的 ProtocolAsyncNioServer 低
 */
@Suppress("MemberVisibilityCanBePrivate")
class AsyncGroupNioServer(
	val port: Int,
	val threads: Int = Runtime.getRuntime().availableProcessors(),
	backlog: Int = 50,
	val handler: suspend AsyncNioSocket.() -> Unit
) : ISocketServer by GroupNioServer(port, threads, object : INioProtocol by AsyncNioSocket.nioSocketProtocol {
	override fun handleConnect(key: SelectionKey, nioThread: INioThread) {
		GlobalScope.launch {
			val socket = AsyncNioSocket(key, nioThread)
			try {
				socket.handler()
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				try {
					nioThread.execute { socket.close() }
				} catch (e: Exception) {
				}
			}
		}
	}
}, backlog)