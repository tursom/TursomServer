package cn.tursom.socket

import cn.tursom.socket.AsyncAioSocket.Companion.defaultTimeout
import cn.tursom.core.buf
import cn.tursom.core.bytebuffer.NioAdvanceByteBuffer
import cn.tursom.core.count
import cn.tursom.core.put
import cn.tursom.core.unSerialize
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.InterruptedByTimeoutException

class AsyncCachedAioSocket(socketChannel: AsynchronousSocketChannel, readBuffer: ByteBuffer, val writeBuffer: ByteBuffer) : AsyncAioSocket(socketChannel) {
	val readBuffer = NioAdvanceByteBuffer(readBuffer)
	
	constructor(socketChannel: AsynchronousSocketChannel) : this(socketChannel, ByteBuffer.allocate(1024), ByteBuffer.allocate(8))
	
	suspend fun write(timeout: Long = 0L): Int {
		return write(writeBuffer, timeout)
	}
	
	suspend fun read(timeout: Long = 0L): Int {
		return read(readBuffer.buffer, timeout)
	}
}


suspend inline fun AsyncCachedAioSocket.recv(
	outputStream: OutputStream,
	readTimeout: Long = 100L,
	firstTimeout: Long = defaultTimeout
) {
	readBuffer.reset(outputStream)
	
	try {
		read(firstTimeout)
		readBuffer.reset(outputStream)
		
		while (read(readTimeout) > 0) {
			readBuffer.reset(outputStream)
		}
	} catch (e: SocketTimeoutException) {
	} catch (e: InterruptedByTimeoutException) {
	}
}

suspend inline fun AsyncCachedAioSocket.recv(
	readTimeout: Long = 100L,
	firstTimeout: Long = defaultTimeout
): ByteArray {
	val byteStream = ByteArrayOutputStream()
	recv(byteStream, readTimeout, firstTimeout)
	return byteStream.toByteArray()
}

suspend inline fun AsyncCachedAioSocket.recvStr(
	charset: String = "utf-8",
	readTimeout: Long = 100L,
	firstTimeout: Long = defaultTimeout
): String {
	val byteStream = ByteArrayOutputStream()
	recv(byteStream, readTimeout, firstTimeout)
	return byteStream.toString(charset)
}

suspend inline fun AsyncCachedAioSocket.recvChar(
	readTimeout: Long = 100L
): Char {
	readBuffer.requireAvailableSize(2)
	while (readBuffer.readableSize < 4) read(readTimeout)
	return readBuffer.getChar()
}

suspend inline fun AsyncCachedAioSocket.recvShort(
	readTimeout: Long = 100L
): Short {
	readBuffer.requireAvailableSize(2)
	while (readBuffer.readableSize < 8) read(readTimeout)
	return readBuffer.getShort()
}

suspend inline fun AsyncCachedAioSocket.recvInt(
	readTimeout: Long = 100L
): Int {
	readBuffer.requireAvailableSize(4)
	while (readBuffer.readableSize < 4) read(readTimeout)
	return readBuffer.getInt()
}

suspend inline fun AsyncCachedAioSocket.recvLong(
	readTimeout: Long = 100L
): Long {
	readBuffer.requireAvailableSize(8)
	while (readBuffer.readableSize < 8) read(readTimeout)
	return readBuffer.getLong()
}

suspend inline fun AsyncCachedAioSocket.recvFloat(
	readTimeout: Long = 100L
): Float {
	readBuffer.requireAvailableSize(4)
	while (readBuffer.readableSize < 4) read(readTimeout)
	return readBuffer.getFloat()
}

suspend inline fun AsyncCachedAioSocket.recvDouble(
	readTimeout: Long = 100L
): Double {
	readBuffer.requireAvailableSize(8)
	while (readBuffer.readableSize < 8) read(readTimeout)
	return readBuffer.getDouble()
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <T> AsyncCachedAioSocket.unSerializeObject(
	readTimeout: Long = 100L,
	firstTimeout: Long = defaultTimeout
): T? {
	val byteArrayOutputStream = ByteArrayOutputStream()
	recv(byteArrayOutputStream, readTimeout, firstTimeout)
	return unSerialize(byteArrayOutputStream.buf, 0, byteArrayOutputStream.count) as T?
}

suspend inline fun AsyncCachedAioSocket.send(message: Int) {
	writeBuffer.clear()
	writeBuffer.array().put(message, writeBuffer.arrayOffset())
	writeBuffer.limit(4)
	write()
}

suspend inline fun AsyncCachedAioSocket.send(message: Long) {
	writeBuffer.clear()
	writeBuffer.array().put(message, writeBuffer.arrayOffset())
	writeBuffer.limit(8)
	write()
}

inline fun <T> AsyncCachedAioSocket.use(crossinline block: suspend AsyncCachedAioSocket.() -> T): T {
	var exception: Throwable? = null
	try {
		return runBlocking { block() }
	} catch (e: Throwable) {
		exception = e
		throw e
	} finally {
		when (exception) {
			null -> close()
			else -> try {
				close()
			} catch (closeException: Throwable) {
				// cause.addSuppressed(closeException) // ignored here
			}
		}
	}
}

inline infix fun AsyncCachedAioSocket.useCachedNonBlock(crossinline block: suspend AsyncCachedAioSocket.() -> Unit) =
	GlobalScope.launch {
		try {
			block()
		} finally {
			try {
				close()
			} catch (closeException: Throwable) {
				// cause.addSuppressed(closeException) // ignored here
			}
		}
	}


suspend inline infix operator fun <T> AsyncCachedAioSocket.invoke(
	@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") block: suspend AsyncCachedAioSocket.() -> T
): T {
	var exception: Throwable? = null
	try {
		return block()
	} catch (e: Throwable) {
		exception = e
		throw e
	} finally {
		when (exception) {
			null -> close()
			else -> try {
				close()
			} catch (closeException: Throwable) {
				// cause.addSuppressed(closeException) // ignored here
			}
		}
	}
}
