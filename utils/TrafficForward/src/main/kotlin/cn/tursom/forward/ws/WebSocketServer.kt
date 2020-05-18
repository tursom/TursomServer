package cn.tursom.forward.ws

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler

open class WebSocketServer(
  val port: Int,
  var handler: (SocketChannel) -> WebSocketHandler,
  var readTimeout: Int? = 60,
  var writeTimeout: Int? = 60,
  var webSocketPath: String = "/ws",
  var bodySize: Int = 512 * 1024
) {
  private val bossGroup: EventLoopGroup = NioEventLoopGroup()
  private val workerGroup: EventLoopGroup = NioEventLoopGroup()
  private var b: ServerBootstrap? = null
  private var future: ChannelFuture? = null

  fun start() {
    b = ServerBootstrap()
      .group(bossGroup, workerGroup)
      .channel(NioServerSocketChannel::class.java)
      .childHandler(object : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
          val pipeline = ch.pipeline()
          if (readTimeout != null) pipeline.addLast(ReadTimeoutHandler(readTimeout!!))
          if (writeTimeout != null) pipeline.addLast(WriteTimeoutHandler(writeTimeout!!))
          pipeline.addLast("codec", HttpServerCodec())
            .addLast("aggregator", HttpObjectAggregator(bodySize))
            .addLast("http-chunked", ChunkedWriteHandler())
          pipeline.addLast("ws", WebSocketServerProtocolHandler(webSocketPath))
          pipeline.addLast("handle", WebSocketFrameHandler(handler(ch)))
        }
      })
    future = b?.bind(port)
    future?.sync()
  }
}