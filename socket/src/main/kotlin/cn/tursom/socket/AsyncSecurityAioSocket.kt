package cn.tursom.socket

import cn.tursom.core.encrypt.AES
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel

class AsyncSecurityAioSocket(
	private val asyncAioSocket: AsyncAioSocket,
	private val key: AES
) : AsyncSocket by asyncAioSocket {
	constructor(socketChannel: AsynchronousSocketChannel, key: AES) : this(AsyncAioSocket(socketChannel), key)

	override suspend fun write(buffer: ByteBuffer, timeout: Long): Int {
		return asyncAioSocket.write(
			ByteBuffer.wrap(key.encrypt(
				buffer.array(),
				buffer.arrayOffset() + buffer.position(),
				buffer.limit()
			)),
			timeout
		)
	}

	suspend fun readBytes(buffer: ByteBuffer, timeout: Long): ByteArray {
		asyncAioSocket.read(buffer, timeout)
		buffer.flip()
		return key.encrypt(
			buffer.array(),
			buffer.arrayOffset() + buffer.position(),
			buffer.limit()
		)
	}
}