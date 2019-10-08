package cn.tursom.socket

import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("MemberVisibilityCanBePrivate")
open class AsyncAioSocket(val socketChannel: AsynchronousSocketChannel) : AsyncSocket {
    val address: SocketAddress get() = socketChannel.remoteAddress

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