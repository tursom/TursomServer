package cn.tursom.socket.server

import cn.tursom.socket.INioProtocol
import cn.tursom.socket.niothread.INioThread
import cn.tursom.socket.niothread.IWorkerGroup
import cn.tursom.socket.niothread.ThreadPoolNioThread
import cn.tursom.socket.niothread.ThreadPoolWorkerGroup
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.util.concurrent.LinkedBlockingDeque

/**
 * 拥有一个连接线程和多个工作线程的 nio 服务器
 */
@Suppress("MemberVisibilityCanBePrivate")
class GroupNioServer(
	val port: Int,
	val threads: Int = Runtime.getRuntime().availableProcessors(),
	private val protocol: INioProtocol,
	backlog: Int = 50,
	val nioThreadGenerator: (
		threadName: String,
		threads: Int,
		worker: (thread: INioThread) -> Unit
	) -> IWorkerGroup = { name, _, worker ->
		ThreadPoolWorkerGroup(threads, name, false, worker)
	}
) : ISocketServer {
	private val listenChannel = ServerSocketChannel.open()
	private val listenThreads = LinkedBlockingDeque<INioThread>()
	private val workerGroupList = LinkedBlockingDeque<IWorkerGroup>()

	init {
		listenChannel.socket().bind(InetSocketAddress(port), backlog)
		listenChannel.configureBlocking(false)
	}

	override fun run() {
		val workerGroup = nioThreadGenerator(
			"nioWorkerGroup", threads,
			NioServer.LoopHandler(protocol)::handle
		)
		workerGroupList.add(workerGroup)

		val nioThread = ThreadPoolNioThread("nioAccepter") { nioThread ->
			val selector = nioThread.selector
			if (selector.isOpen) {
                forEachKey(selector) { key ->
                    try {
                        when {
                            key.isAcceptable -> {
                                val serverChannel = key.channel() as ServerSocketChannel
                                var channel = serverChannel.accept()
                                while (channel != null) {
                                    channel.configureBlocking(false)
                                    workerGroup.register(channel) { (key, thread) ->
                                        protocol.handleConnect(key, thread)
                                    }
                                    channel = serverChannel.accept()
                                }
                            }
                        }
                    } catch (e: Throwable) {
                        try {
                            protocol.exceptionCause(key, nioThread, e)
                        } catch (e1: Throwable) {
                            e.printStackTrace()
                            e1.printStackTrace()
                            key.cancel()
                            key.channel().close()
                        }
                    }
                    nioThread.execute(this)
                }
			}
		}
		listenThreads.add(nioThread)
		listenChannel.register(nioThread.selector, SelectionKey.OP_ACCEPT)
		nioThread.wakeup()
	}

	override fun close() {
		listenChannel.close()
		listenThreads.forEach { it.close() }
		workerGroupList.forEach { it.close() }
	}

	companion object {
		const val TIMEOUT = 3000L

		inline fun forEachKey(selector: Selector, action: (key: SelectionKey) -> Unit) {
			if (selector.select(TIMEOUT) != 0) {
				val keyIter = selector.selectedKeys().iterator()
				while (keyIter.hasNext()) run whileBlock@{
					val key = keyIter.next()
					keyIter.remove()
					action(key)
				}
			}
		}
	}
}