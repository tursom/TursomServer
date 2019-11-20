package cn.tursom.web.netty

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.web.ExceptionContent
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext

class NettyExceptionContent(
	val ctx: ChannelHandlerContext,
	override val cause: Throwable
) : ExceptionContent {
	override fun write(message: String) {
		ctx.write(Unpooled.wrappedBuffer(message.toByteArray()))
	}

	override fun write(bytes: ByteArray, offset: Int, length: Int) {
		ctx.write(Unpooled.wrappedBuffer(bytes, offset, length))
	}

	override fun write(buffer: ByteBuffer) {
		when (buffer) {
			is NettyByteBuffer -> ctx.write(buffer.byteBuf)
			else -> write(buffer.getBytes())
		}
	}

	override fun finish() {
		ctx.flush()
	}
}