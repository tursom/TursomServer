package cn.tursom.socket

import cn.tursom.core.*
import cn.tursom.socket.client.AsyncClient
import cn.tursom.socket.server.async.AsyncSocketServer
import cn.tursom.core.bytebuffer.HeapByteBuffer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.nio.channels.InterruptedByTimeoutException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

open class AsyncAioSocket(private val socketChannel: AsynchronousSocketChannel) : AsyncSocket {
	val address get() = socketChannel.remoteAddress

	fun cached() = AsyncCachedAioSocket(socketChannel)

	override suspend fun write(buffer: ByteBuffer, timeout: Long): Int {
		return suspendCoroutine { cont ->
			this.socketChannel.write(buffer, timeout, TimeUnit.MILLISECONDS, cont, awaitHandler)
		}
	}

	override suspend fun read(buffer: ByteBuffer, timeout: Long): Int {
		return suspendCoroutine { cont ->
			this.socketChannel.read(buffer, timeout, TimeUnit.MILLISECONDS, cont, awaitHandler)
		}
	}

	override suspend fun write(buffer: Array<out ByteBuffer>, timeout: Long): Long {
		return suspendCoroutine { cont ->
			this.socketChannel.write(buffer, 0, buffer.size, timeout, TimeUnit.MILLISECONDS, cont, awaitLongHandler)
		}
	}

	override suspend fun read(buffer: Array<out ByteBuffer>, timeout: Long): Long {
		return suspendCoroutine { cont ->
			this.socketChannel.read(buffer, 0, buffer.size, timeout, TimeUnit.MILLISECONDS, cont, awaitLongHandler)
		}
	}

	override fun close() {
		socketChannel.close()
	}

	fun reset() {
		val field = socketChannel.javaClass.getDeclaredField("readKilled")
		field.isAccessible = true
		field.set(socketChannel, true)
	}

	companion object {
		const val defaultTimeout = 60_000L

		@JvmStatic
		private val awaitHandler =
			object : CompletionHandler<Int, Continuation<Int>> {
				override fun completed(result: Int, attachment: Continuation<Int>) {
					attachment.resume(result)
				}

				override fun failed(exc: Throwable, attachment: Continuation<Int>) {
					attachment.resumeWithException(exc)
				}
			}

		@JvmStatic
		private val awaitLongHandler =
			object : CompletionHandler<Long, Continuation<Long>> {
				override fun completed(result: Long, attachment: Continuation<Long>) {
					attachment.resume(result)
				}

				override fun failed(exc: Throwable, attachment: Continuation<Long>) {
					attachment.resumeWithException(exc)
				}
			}
	}
}

@Suppress("unused")
suspend inline fun AsyncAioSocket.write(message: String, timeout: Long = 0L) =
	write(ByteBuffer.wrap(message.toByteArray()), timeout)

suspend inline fun AsyncAioSocket.recvStr(buffer: ByteBuffer, timeout: Long = 0L): String {
	//readBuffer.clear()
	read(buffer, timeout)
	return String(buffer.array(), buffer.arrayOffset(), buffer.position())
}


suspend inline fun <T : OutputStream> AsyncAioSocket.recv(
	outputStream: T,
	readTimeout: Long = 100L,
	firstTimeout: Long = AsyncAioSocket.defaultTimeout,
	buffer: ByteBuffer = ByteBuffer.allocate(1024)
): T {
	buffer.clear()

	try {
		if (read(buffer, firstTimeout) <= 0) throw InterruptedByTimeoutException()
		@Suppress("BlockingMethodInNonBlockingContext")
		outputStream.write(buffer.array(), buffer.arrayOffset(), buffer.position())
		buffer.clear()

		while (read(buffer, readTimeout) > 0) {
			@Suppress("BlockingMethodInNonBlockingContext")
			outputStream.write(buffer.array(), buffer.arrayOffset(), buffer.position())
			buffer.clear()
		}
	} catch (e: InterruptedByTimeoutException) {
	}

	return outputStream
}

suspend inline fun AsyncAioSocket.recv(
	readTimeout: Long = 100L,
	firstTimeout: Long = AsyncAioSocket.defaultTimeout,
	buffer: ByteBuffer = ByteBuffer.allocate(1024)
): ByteArray {
	buffer.clear()
	val byteStream = ByteArrayOutputStream()
	recv(byteStream, readTimeout, firstTimeout, buffer)
	return byteStream.toByteArray()
}

suspend inline fun AsyncAioSocket.recvStr(
	readTimeout: Long = 100L,
	firstTimeout: Long = AsyncAioSocket.defaultTimeout,
	buffer: ByteBuffer = ByteBuffer.allocate(1024)
): String {
	buffer.clear()
	val byteStream = ByteArrayOutputStream()
	recv(byteStream, readTimeout, firstTimeout, buffer)
	return String(byteStream.buf, 0, byteStream.count)
}

suspend inline fun AsyncAioSocket.recvInt(
	readTimeout: Long = 100L,
	firstTimeout: Long = AsyncAioSocket.defaultTimeout,
	buffer: ByteBuffer = ByteBuffer.allocate(4)
): Int {
	buffer.clear().limit(4)
	var readSize = read(buffer, firstTimeout)
	while (readSize < 8) {
		readSize += read(buffer, readTimeout)
	}
	return buffer.array().toInt(buffer.arrayOffset())
}

suspend inline fun AsyncAioSocket.recvLong(
	readTimeout: Long = 100L,
	firstTimeout: Long = AsyncAioSocket.defaultTimeout,
	buffer: ByteBuffer = ByteBuffer.allocate(8)
): Long {
	buffer.clear().limit(8)
	var readSize = read(buffer, firstTimeout)
	while (readSize < 8) {
		readSize += read(buffer, readTimeout)
	}
	return buffer.array().toLong(buffer.arrayOffset())
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <T> AsyncAioSocket.unSerializeObject(
	buffer: ByteBuffer = ByteBuffer.allocate(1024),
	readTimeout: Long = 100L,
	firstTimeout: Long = AsyncAioSocket.defaultTimeout
): T? {
	return recv(ByteArrayOutputStream(), readTimeout, firstTimeout, buffer).let { unSerialize(
        it.buf,
        0,
        it.count
    ) as T? }
}

suspend inline fun AsyncAioSocket.send(message: ByteArray?, offset: Int = 0, size: Int = message?.size ?: 0) {
	write(HeapByteBuffer.wrap(message ?: return, offset, size))
}

suspend inline fun AsyncAioSocket.send(message: String?) {
	send((message ?: return).toByteArray())
}

suspend inline fun AsyncAioSocket.send(message: Int, buffer: ByteArray = ByteArray(4)) {
	buffer.put(message)
	send(buffer)
}

suspend inline fun AsyncAioSocket.send(message: Long, buffer: ByteArray = ByteArray(8)) {
	buffer.put(message)
	send(buffer)
}

suspend fun AsyncAioSocket.sendObject(obj: Any?): Int {
	val byteArrayOutputStream = ByteArrayOutputStream()
    serialize(byteArrayOutputStream, obj ?: return -1)
	send(byteArrayOutputStream.buf, 0, byteArrayOutputStream.count)
	return byteArrayOutputStream.count
}

inline fun <T> AsyncAioSocket.use(crossinline block: suspend AsyncAioSocket.() -> T): T {
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

inline infix fun AsyncAioSocket.useNonBlock(crossinline block: suspend AsyncAioSocket.() -> Unit) =
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

suspend inline infix operator fun <T> AsyncAioSocket.invoke(
	@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") block: suspend AsyncAioSocket.() -> T
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

fun main() {
	val port = 12345
	val server = AsyncSocketServer(port) {
		val buffer = ByteBuffer.allocate(1024)
		while (true) {
			buffer.clear()
			read(buffer)
			buffer.flip()
			println("recv [${buffer.limit()}]")
			write(buffer)
		}
	}
	server.run()

	val input = System.`in`.bufferedReader()
	runBlocking {
		val client = AsyncClient.connect("127.0.0.1", port)
		val buffer = ByteBuffer.allocate(1024)
		while (true) {
			@Suppress("BlockingMethodInNonBlockingContext") val line = input.readLine()
			println("sending [${line.length}]")
			client.send(line)
			while (client.read(buffer) == buffer.limit()) {
				println("client recv [${buffer.position()}]")
				println(String(buffer.array(), 0, buffer.position()))
				buffer.clear()
			}
			println("client recv [${buffer.position()}]")
			println(String(buffer.array(), 0, buffer.position()))
			buffer.clear()
		}
	}
}
