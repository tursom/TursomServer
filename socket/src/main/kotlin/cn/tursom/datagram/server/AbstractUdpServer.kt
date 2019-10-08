package cn.tursom.datagram.server

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress

abstract class AbstractUdpServer : UDPServer {
	protected abstract val socket: DatagramSocket
	
	fun send(address: SocketAddress, buffer: ByteArray, size: Int = buffer.size, offset: Int = 0) {
		socket.send(DatagramPacket(buffer, offset, size, address))
	}
}