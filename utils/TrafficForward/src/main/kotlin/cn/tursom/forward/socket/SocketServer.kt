package cn.tursom.forward.socket

import cn.tursom.forward.NettyHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler

open class SocketServer(
  val port: Int,
  var handler: (SocketChannel) -> NettyHandler,
  var readTimeout: Int? = 60,
  var writeTimeout: Int? = 60
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
          try {
            pipeline.addLast("handle", SocketServerHandler(handler(ch)))
          } catch (e: Exception) {
            ch.close()
          }
        }
      })
    future = b?.bind(port)
    future?.sync()
  }
}