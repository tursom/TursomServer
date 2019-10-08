package cn.tursom.datagram.server

import cn.tursom.core.timer.WheelTimer
import cn.tursom.datagram.UdpPackageSize
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress
import java.net.SocketException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeoutException

class MultiThreadUDPServer(
	override val port: Int,
	val thread: Int = Runtime.getRuntime().availableProcessors(),
	private val packageSize: Int = UdpPackageSize.defaultLen,
	private val exception: Exception.() -> Unit = { printStackTrace() },
	private val handler: MultiThreadUDPServer.(address: SocketAddress, buffer: ByteArray, size: Int) -> Unit
) : AbstractUdpServer() {
	private val excWheelTimer = WheelTimer.timer
	private val connectionMap: java.util.AbstractMap<
		SocketAddress,
		MultiThreadUDPServer.(
			address: SocketAddress,
			buffer: ByteArray,
			size: Int
		) -> Unit
		> = ConcurrentHashMap()
	
	override val socket = DatagramSocket(port)
	
	override fun run() {
		val inBuff = ByteArray(packageSize)
		val inPacket = DatagramPacket(inBuff, inBuff.size)
		while (true) {
			try {
				//读取inPacket的数据
				socket.receive(inPacket)
				val address = inPacket.socketAddress
				(connectionMap[address] ?: handler)(address, inPacket.data, inPacket.length)
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
	
	override fun start() {
		for (i in 1..thread) {
			Thread(this, "MTUdpSer$i").start()
		}
	}
	
	@Suppress("NAME_SHADOWING")
	fun recv(
		address: SocketAddress,
		timeout: Long = 0L,
		onTimeout: (e: Exception) -> Unit = { it.printStackTrace() },
		handler: MultiThreadUDPServer.(address: SocketAddress, buffer: ByteArray, size: Int) -> Unit
	) {
		val timeoutTask = if (timeout > 0L) {
			excWheelTimer.exec(timeout) {
				onTimeout(TimeoutException())
			}
		} else {
			null
		}
		connectionMap[address] = { address: SocketAddress, buffer: ByteArray, size: Int ->
			timeoutTask?.cancel()
			handler(address, buffer, size)
		}
	}
	
	override fun close() {
		socket.close()
	}
}