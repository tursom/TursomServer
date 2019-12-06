package cn.tursom.web.netty.ws

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest

class NettyWsHttpContent(
  val ctx: ChannelHandlerContext,
  val msg: FullHttpRequest
)