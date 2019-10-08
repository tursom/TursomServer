package cn.tursom.datagram.server

import cn.tursom.core.timer.WheelTimer
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.concurrent.*

class AioUdpServer(
    override val port: Int,
    private val threadPool: ThreadPoolExecutor = ThreadPoolExecutor(
        1,
        1,
        0L,
        TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(32)
    ),
    private val queue: BlockingQueue<() -> Unit> = ArrayBlockingQueue(128),
    private val handler: AioUdpServer.(channel: DatagramChannel, address: SocketAddress, buffer: ByteBuffer) -> Unit
) : UDPServer {
    private val excWheelTimer = WheelTimer.timer
    private val channel = DatagramChannel.open()!!
    private val selector = Selector.open()!!
    private var closed: Boolean = false
    private val connectionMap = ConcurrentHashMap<SocketAddress, AioUdpServer.(
        channel: DatagramChannel,
        address: SocketAddress,
        buffer: ByteBuffer
    ) -> Unit>()

    init {
        channel.configureBlocking(false)
        channel.socket().bind(InetSocketAddress(port))
        channel.register(selector, SelectionKey.OP_READ)
    }

    override fun run() {
        val byteBuffer = ByteBuffer.allocateDirect(2048)

        while (!closed) {
            try {
                val taskQueue = queue.iterator()
                while (taskQueue.hasNext()) {
                    taskQueue.next()()
                    taskQueue.remove()
                }

                // 进行选择
                val select = selector.select(60000)
                if (select > 0) {
                    // 获取以选择的键的集合
                    val iterator = selector.selectedKeys().iterator()

                    while (iterator.hasNext()) {
                        val key = iterator.next() as SelectionKey
                        // 必须手动删除
                        iterator.remove()
                        if (key.isReadable) {
                            val datagramChannel = key.channel() as DatagramChannel
                            // 读取
                            byteBuffer.clear()
                            println(datagramChannel === channel)
                            val address = datagramChannel.receive(byteBuffer) ?: continue
                            val action =
                                connectionMap[address] ?: handler
                            threadPool.execute { action(datagramChannel, address, byteBuffer) }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun start() {
        Thread(this, "AioUdpSer").start()
    }

    override fun close() {
        closed = true
        channel.close()
        threadPool.shutdown()
        selector.close()
    }

    fun read(
		address: SocketAddress,
		timeout: Long = 0L,
		exc: (e: Exception) -> Unit = { it.printStackTrace() },
		onComplete: (byteBuffer: ByteBuffer) -> Unit
	) {
        val timeoutTask = if (timeout > 0) {
            excWheelTimer.exec(timeout) {
				connectionMap.remove(address)
				exc(TimeoutException("cn.tursom.datagram address $address read time out"))
			}
		} else {
            null
        }
        connectionMap[address] = { _, _, buffer ->
            timeoutTask?.cancel()
            onComplete(buffer)
        }
    }


    fun send(
        channel: DatagramChannel,
        address: SocketAddress,
        buffer: ByteBuffer
    ) {
        channel.send(buffer, address)
    }
}