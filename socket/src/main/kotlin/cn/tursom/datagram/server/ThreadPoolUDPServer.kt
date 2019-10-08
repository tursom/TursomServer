package cn.tursom.datagram.server

import cn.tursom.core.timer.WheelTimer
import cn.tursom.datagram.UdpPackageSize
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress
import java.net.SocketException
import java.util.concurrent.*

/**
 * 单线程UDP服务器
 */
class ThreadPoolUDPServer(
    override val port: Int,
    private val connectionMap: java.util.AbstractMap<
            SocketAddress,
            ThreadPoolUDPServer.(
                address: SocketAddress,
                buffer: ByteArray,
                size: Int
            ) -> Unit
            > = HashMap(),
    private val queue: BlockingQueue<() -> Unit> = LinkedBlockingQueue(128),
    private val packageSize: Int = UdpPackageSize.defaultLen,
    private val threadPool: ThreadPoolExecutor = ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 2,
        60_000L,
        TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(32)
    ),
    private val exception: Exception.() -> Unit = { printStackTrace() },
    private val handler: ThreadPoolUDPServer.(address: SocketAddress, buffer: ByteArray, size: Int) -> Unit
) : AbstractUdpServer() {
    private val excWheelTimer = WheelTimer.timer

    override val socket = DatagramSocket(port)

    override fun run() {
        val inBuff = ByteArray(packageSize)
        val inPacket = DatagramPacket(inBuff, inBuff.size)
        while (true) {
            try {
                val taskQueue = queue.iterator()
                while (taskQueue.hasNext()) {
                    try {
                        taskQueue.next()()
                    } catch (e: Exception) {
                        e.exception()
                    }
                    taskQueue.remove()
                }

                //读取inPacket的数据
                socket.receive(inPacket)
                val address = inPacket.socketAddress
                val handler = (connectionMap[address] ?: handler)
                threadPool.execute {
                    this.handler(address, inPacket.data, inPacket.length)
                }
            } catch (e: SocketException) {
                if (e.message == "Socket closed" || e.message == "cn.tursom.socket closed") {
                    break
                } else {
                    e.exception()
                }
            } catch (e: Exception) {
                e.exception()
            }
        }
    }

    fun recv(
		address: SocketAddress,
		timeout: Long = 0L,
		onTimeout: (e: Exception) -> Unit = { it.printStackTrace() },
		handler: ThreadPoolUDPServer.(address: SocketAddress, buffer: ByteArray, size: Int) -> Unit
	) {
        val timeoutTask = if (timeout > 0L) {
            excWheelTimer.exec(timeout) {
                onTimeout(TimeoutException())
            }
        } else {
            null
        }
        queue.add {
            connectionMap[address] = { address: SocketAddress, buffer: ByteArray, size: Int ->
                timeoutTask?.cancel()
                handler(address, buffer, size)
            }
        }
    }

    override fun start() {
        Thread(this, "TPUdpSer").start()
    }

    override fun close() {
        socket.close()
    }
}

