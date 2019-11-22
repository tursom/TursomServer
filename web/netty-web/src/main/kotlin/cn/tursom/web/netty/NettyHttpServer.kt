package cn.tursom.web.netty

import cn.tursom.web.HttpHandler
import cn.tursom.web.HttpServer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.stream.ChunkedWriteHandler

class NettyHttpServer(
  override val port: Int,
  handler: HttpHandler<NettyHttpContent, NettyExceptionContent>,
  bodySize: Int = 512 * 1024,
  autoRun: Boolean = false
) : HttpServer {
  constructor(
    port: Int,
    bodySize: Int = 512 * 1024,
    autoRun: Boolean = false,
    handler: (content: NettyHttpContent) -> Unit
  ) : this(
    port,
    object : HttpHandler<NettyHttpContent, NettyExceptionContent> {
      override fun handle(content: NettyHttpContent) = handler(content)
    },
    bodySize,
    autoRun
  )

  val httpHandler = NettyHttpHandler(handler)
  private val group = NioEventLoopGroup()
  private val b = ServerBootstrap().group(group)
    .channel(NioServerSocketChannel::class.java)
    .childHandler(object : ChannelInitializer<SocketChannel>() {
      override fun initChannel(ch: SocketChannel) {
        ch.pipeline()
          .addLast("decoder", HttpRequestDecoder())
          .addLast("encoder", HttpResponseEncoder())
          .addLast("aggregator", HttpObjectAggregator(bodySize))
          .addLast("http-chunked", ChunkedWriteHandler())
          .addLast("handle", httpHandler)
      }
    })
    .option(ChannelOption.SO_BACKLOG, 1024) // determining the number of connections queued
    .option(ChannelOption.SO_REUSEADDR, true)
    .childOption(ChannelOption.SO_KEEPALIVE, java.lang.Boolean.TRUE)
  private val future: ChannelFuture = b.bind(port)

  init {
    if (autoRun) run()
  }

  override fun run() {
    future.sync()
  }

  override fun close() {
    future.cancel(false)
    future.channel().close()
  }
}