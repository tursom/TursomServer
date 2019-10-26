package cn.tursom.datagram.broadcast

import cn.tursom.core.toUTF8String
import cn.tursom.datagram.UdpPackageSize.LANNetLen
import java.lang.Thread.sleep
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import kotlin.concurrent.thread

class BroadcastServer(val port: Int, val bufSize: Int = LANNetLen) {
  private val socket = DatagramSocket()
  private val server by lazy { DatagramSocket(port) }
  private val buffer = DatagramPacket(ByteArray(bufSize), bufSize)

  constructor(port: Int) : this(port, LANNetLen)

  private fun send(packet: DatagramPacket) {
    socket.send(packet)
  }

  fun send(packet: ByteArray) {
    send(packet, 0, packet.size)
  }

  fun send(packet: ByteArray, offset: Int = 0, size: Int = packet.size) {
    send(DatagramPacket(packet, offset, size, broadcastInetAddr, port))
  }

  fun recv(): ByteArray {
    server.receive(buffer)
    return Arrays.copyOfRange(buffer.data, 0, buffer.length)
  }

  fun recvBuffer(): DatagramPacket {
    server.receive(buffer)
    return buffer
  }

  companion object {
    const val BROADCAST_IP = "255.255.255.255"
    @JvmStatic
    val broadcastInetAddr = InetAddress.getByName(BROADCAST_IP)

    @JvmStatic
    fun takeOut(packet: DatagramPacket) = Arrays.copyOfRange(packet.data, 0, packet.length)
  }
}