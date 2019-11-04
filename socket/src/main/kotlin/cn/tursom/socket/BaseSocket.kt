package cn.tursom.socket

import cn.tursom.core.put
import java.io.Closeable
import java.net.Socket

/**
 * 对基础的Socket做了些许封装
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class BaseSocket(
    val socket: Socket,
    val timeout: Int = Companion.timeout
) : Closeable {
  
  val address = socket.inetAddress?.toString()?.drop(1) ?: "0.0.0.0"
  val port = socket.port
  val localPort = socket.localPort
  val inputStream by lazy { socket.getInputStream()!! }
  val outputStream by lazy { socket.getOutputStream()!! }
  
  fun send(message: String?) {
    send((message ?: return).toByteArray())
  }
  
  fun send(message: ByteArray?) {
    outputStream.write(message ?: return)
  }
  
  fun send(message: Int) {
    val buffer = ByteArray(4)
    buffer.put(message)
    send(buffer)
  }
  
  fun send(message: Long) {
    val buffer = ByteArray(8)
    buffer.put(message)
    send(buffer)
  }
  
  override fun close() {
    closeSocket()
  }
  
  protected fun closeSocket() {
    if (!socket.isClosed) {
      closeInputStream()
      closeOutputStream()
      socket.close()
    }
  }
  
  private fun closeInputStream() {
    try {
      inputStream.close()
    } catch (e: Exception) {
    }
  }
  
  private fun closeOutputStream() {
    try {
      outputStream.close()
    } catch (e: Exception) {
    }
  }
  
  fun isConnected(): Boolean {
    return socket.isConnected
  }
  
  companion object Companion {
    const val defaultReadSize: Int = 1024 * 8
    const val timeout: Int = 60 * 1000
  }
}