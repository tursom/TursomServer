package cn.tursom.web.netty

import cn.tursom.web.HttpHandler
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpRequest

@ChannelHandler.Sharable
class NettyHttpHandler(
	private val handler: HttpHandler<NettyHttpContent, NettyExceptionContent>
) : SimpleChannelInboundHandler<FullHttpRequest>() {

	override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest) {
		val handlerContext = NettyHttpContent(ctx, msg)
		try {
			handler.handle(handlerContext)
		} catch (e: Throwable) {
			handlerContext.write("${e.javaClass}: ${e.message}")
		}
	}

	override fun channelReadComplete(ctx: ChannelHandlerContext) {
		super.channelReadComplete(ctx)
		ctx.flush()
	}

	override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable?) {
		if (cause != null) handler.exception(NettyExceptionContent(ctx, cause))
		ctx.close()
	}
}