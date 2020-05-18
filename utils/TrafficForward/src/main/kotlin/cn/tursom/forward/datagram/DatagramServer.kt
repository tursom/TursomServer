package cn.tursom.forward.datagram

import cn.tursom.forward.NettyHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.DefaultEventLoopGroup
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler

open class DatagramServer(
  val port: Int,
  var handler: (DatagramChannel) -> NettyHandler,
  var readTimeout: Int? = 60,
  var writeTimeout: Int? = 60
) {
  private val bossGroup: EventLoopGroup = NioEventLoopGroup()
  private val workerGroup: EventLoopGroup = DefaultEventLoopGroup()
  private var b: ServerBootstrap? = null
  private var future: ChannelFuture? = null

  fun start() {
    b = ServerBootstrap()
      .group(bossGroup, workerGroup)
      .channel(ServerDatagramChannel::class.java)
      .childHandler(object : ChannelInitializer<DatagramChannel>() {
        override fun initChannel(ch: DatagramChannel) {
          val pipeline = ch.pipeline()
          if (readTimeout != null) pipeline.addLast(ReadTimeoutHandler(readTimeout!!))
          if (writeTimeout != null) pipeline.addLast(WriteTimeoutHandler(writeTimeout!!))
          pipeline.addLast("handle", DatagramServerHandler(handler(ch)))
        }
      })
    future = b?.bind(port)
    future?.sync()
  }
}