package cn.tursom.core.ws

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

@ChannelHandler.Sharable
object WebSocketFrameWrapper : ChannelOutboundHandlerAdapter() {
    override fun write(ctx: ChannelHandlerContext, msg: Any?, promise: ChannelPromise?) {
        ctx.write(when (msg) {
            is String -> TextWebSocketFrame(msg)
            is ByteArray -> BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg))
            is ByteBuf -> BinaryWebSocketFrame(msg)
            else -> msg
        }, promise)
    }
}