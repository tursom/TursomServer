package cn.tursom.web.netty

import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.stream.ChunkedInput

class NettyChunkedByteBuffer(val bufList: List<AdvanceByteBuffer>) : ChunkedInput<ByteBuf> {
	constructor(vararg bufList: AdvanceByteBuffer) : this(bufList.asList())

	var iterator = bufList.iterator()
	var progress: Long = 0
	val length = run {
		var len = 0L
		bufList.forEach {
			len += it.readableSize
		}
		len
	}

	override fun progress(): Long = progress
	override fun length(): Long = length
	override fun isEndOfInput(): Boolean = !iterator.hasNext()

	override fun readChunk(ctx: ChannelHandlerContext?): ByteBuf = readChunk()
	override fun readChunk(allocator: ByteBufAllocator?): ByteBuf = readChunk()

	private fun readChunk(): ByteBuf {
		val next = iterator.next()
		progress += next.readableSize
		return if (next is NettyAdvanceByteBuffer) next.byteBuf
		else Unpooled.wrappedBuffer(next.nioBuffer)
	}

	override fun close() {}
}

