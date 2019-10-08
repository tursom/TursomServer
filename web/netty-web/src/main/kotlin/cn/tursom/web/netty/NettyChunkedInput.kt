package cn.tursom.web.netty

import cn.tursom.web.utils.Chunked
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.stream.ChunkedInput

class NettyChunkedInput(val chunked: Chunked) : ChunkedInput<ByteBuf> {
	override fun progress(): Long = chunked.progress
	override fun length() = chunked.length
	override fun isEndOfInput(): Boolean = chunked.endOfInput

	override fun readChunk(ctx: ChannelHandlerContext?): ByteBuf {
		val buf = chunked.readChunk()
		return if (buf is NettyAdvanceByteBuffer) buf.byteBuf
		else Unpooled.wrappedBuffer(buf.nioBuffer)
	}

	override fun readChunk(allocator: ByteBufAllocator?): ByteBuf {
		val buf = chunked.readChunk()
		return if (buf is NettyAdvanceByteBuffer) buf.byteBuf
		else Unpooled.wrappedBuffer(buf.nioBuffer)
	}

	override fun close() = chunked.close()
}