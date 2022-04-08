package cn.tursom.web.client.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory

object HttpExecutor {
  private val group = NioEventLoopGroup()

  fun group(
    host: String,
    port: Int,
    ssl: Boolean,
    initChannel: (SocketChannel) -> Unit = {},
  ): suspend () -> NettyHttpConnection {
    val sslCtx = if (ssl) {
      SslContextBuilder.forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE).build()
    } else {
      null
    }
    val bootstrap = Bootstrap()
      .group(group)
      .channel(NioSocketChannel::class.java)
      .handler(object : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
          ch.pipeline().apply {
            if (sslCtx != null) {
              addLast(sslCtx.newHandler(ch.alloc(), host, port))
            }
            addLast(HttpClientCodec())
          }
          initChannel(ch)
        }

        override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
          super.exceptionCaught(ctx, cause)
        }
      })
    return {
      val channelFuture = bootstrap.connect(host, port)
      channelFuture.awaitSuspend()
      HttpClientImpl(channelFuture.channel() as SocketChannel)
    }
  }

  suspend fun connect(
    host: String,
    port: Int,
    ssl: Boolean,
    initChannel: (SocketChannel) -> Unit = {},
  ): NettyHttpConnection {
    return group(host, port, ssl, initChannel)()
  }

  private class HttpClientImpl(
    override val channel: SocketChannel,
  ) : NettyHttpConnection
}
