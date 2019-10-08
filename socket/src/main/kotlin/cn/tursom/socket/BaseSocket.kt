package cn.tursom.socket

import cn.tursom.core.*
import java.io.*
import java.net.Socket
import java.net.SocketTimeoutException

/**
 * 对基础的Socket做了些许封装
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class BaseSocket(
	private val socket: Socket,
	private val timeout: Int = Companion.timeout
) : Closeable {
	
	val address = socket.inetAddress?.toString()?.drop(1) ?: "0.0.0.0"
	val port = socket.port
	val localPort = socket.localPort
	private val inputStream = socket.getInputStream()!!
	private val outputStream = socket.getOutputStream()!!
	
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
	
	fun sendObject(obj: Any?): Boolean {
		send(serialize(obj ?: return false) ?: return false)
		return true
	}
	
	inline fun <reified T> recvObject(): T? {
		return try {
			unSerialize(recv()) as T
		} catch (e: Exception) {
			null
		}
	}
	
	fun recvString(
		readTimeout: Int = 100,
		firstTimeout: Int = timeout
	): String {
		return recv(readTimeout, firstTimeout).toUTF8String()
	}
	
	fun recvString(
		maxsize: Int,
		readTimeout: Int = 100,
		firstTimeout: Int = timeout
	): String {
		return recv(maxsize, readTimeout, firstTimeout).toUTF8String()
	}
	
	fun recvInt(
		timeout1: Int = timeout
	): Int? {
		val buffer = ByteArray(4)
		socket.soTimeout = timeout1
		var sTime = System.currentTimeMillis()
		//读取数据
		var rSize = inputStream.read(buffer, 0, 4)
		while (rSize < 4) {
			val sTime2 = System.currentTimeMillis()
			socket.soTimeout -= (sTime2 - sTime).toInt()
			sTime = sTime2
			val sReadSize = inputStream.read(buffer, rSize, 8 - rSize)
			if (sReadSize <= 0) {
				break
			} else {
				rSize += sReadSize
			}
		}
		return buffer.toInt()
	}
	
	fun recvLong(
		timeout1: Int = timeout
	): Long? {
		val buffer = ByteArray(8)
		socket.soTimeout = timeout1
		var sTime = System.currentTimeMillis()
		//读取数据
		var rSize = inputStream.read(buffer, 0, 8)
		while (rSize < 4) {
			val sTime2 = System.currentTimeMillis()
			socket.soTimeout -= (sTime2 - sTime).toInt()
			sTime = sTime2
			val sReadSize = inputStream.read(buffer, rSize, 8 - rSize)
			if (sReadSize <= 0) {
				break
			} else {
				rSize += sReadSize
			}
		}
		return buffer.toLong()
	}
	
	fun recv(
		readTimeout: Int = 100,
		firstTimeout: Int = timeout
	): ByteArray {
		val outputStream = ByteArrayOutputStream()
		recv(outputStream, readTimeout, firstTimeout)
		return outputStream.toByteArray()
	}
	
	fun recv(
		maxsize: Int,
		readTimeout: Int = 100,
		firstTimeout: Int = timeout
	): ByteArray {
		val buffer = ByteArray(maxsize)
		var readSize = 0
		socket.soTimeout = firstTimeout
		
		try {
			readSize = inputStream.read(buffer)
			
			socket.soTimeout = readTimeout
			while (readSize < buffer.size) {
				val sReadSize = inputStream.read(buffer, readSize, buffer.size - readSize)
				if (sReadSize <= 0) {
					break
				} else {
					readSize += sReadSize
				}
			}
		} catch (e: SocketTimeoutException) {
		}
		return buffer.copyOf(readSize)
	}
	
	fun recv(
		outputStream: OutputStream,
		readTimeout: Int = 100,
		firstTimeout: Int = timeout
	) {
		val buffer = ByteArray(1024)
		socket.soTimeout = firstTimeout
		
		try {
			val readSize = inputStream.read(buffer)
			if (readSize < 0) {
				throw IOException("cannot read data")
			}
			outputStream.write(buffer, 0, readSize)
			socket.soTimeout = readTimeout
			while (true) {
				val sReadSize = inputStream.read(buffer)
				if (sReadSize <= 0) {
					break
				} else {
					outputStream.write(buffer, 0, readSize)
				}
			}
		} catch (e: SocketTimeoutException) {
		}
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