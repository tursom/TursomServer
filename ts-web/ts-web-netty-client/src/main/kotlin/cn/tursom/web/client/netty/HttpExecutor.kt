package cn.tursom.web.client.netty

import cn.tursom.core.seconds
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpContentDecompressor
import io.netty.handler.codec.http2.*
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.ssl.*
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import kotlinx.coroutines.delay


object HttpExecutor {
  private val sslProvider = when {
    SslProvider.isAlpnSupported(SslProvider.OPENSSL) -> SslProvider.OPENSSL
    else -> SslProvider.JDK
  }
  private val group = NioEventLoopGroup {
    val thread = Thread(it)
    thread.isDaemon = true
    thread
  }

  fun group(
    host: String,
    port: Int,
    ssl: Boolean,
    initChannel: (SocketChannel) -> Unit = {},
  ): suspend () -> NettyHttpConnection {
    val sslCtx = if (ssl) {
      SslContextBuilder.forClient()
        .sslProvider(sslProvider)
        .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .applicationProtocolConfig(ApplicationProtocolConfig(
          ApplicationProtocolConfig.Protocol.ALPN,
          // NO_ADVERTISE is currently the only mode supported by both OpenSsl and JDK providers.
          ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
          // ACCEPT is currently the only mode supported by both OpenSsl and JDK providers.
          ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
          ApplicationProtocolNames.HTTP_2, ApplicationProtocolNames.HTTP_1_1
        ))
        .build()
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
              addLast(Http2FrameCodecBuilder.forClient()
                // unnecessary
                //.initialSettings(Http2Settings.defaultSettings())
                .build())
            }
            addLast(LoggingHandler(LogLevel.INFO))
            addLast(HttpClientCodec())
            addLast(HttpContentDecompressor())
            addLast(Http2MultiplexHandler(object : SimpleChannelInboundHandler<Any>() {
              override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
                // 处理inbound streams
                println("Http2MultiplexHandler接收到消息: $msg")
              }
            }))
          }
          initChannel(ch)
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

suspend fun main() {
  val group = HttpExecutor.group("api.vc.bilibili.com", 443, true)
  val connection = group()
  val bootstrap = Http2StreamChannelBootstrap(connection.channel)
  val streamChannel = bootstrap.open().syncUninterruptibly().now
  //val streamFrameResponseHandler = Http2ClientStreamFrameHandler()
  streamChannel.pipeline()
    .addLast(object : SimpleChannelInboundHandler<Any>() {
      override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
        // 处理inbound streams
        println("Http2MultiplexHandler接收到消息: $msg")
      }
    })
  val headers = DefaultHttp2Headers()
  headers.method("GET")
  headers.path("/dynamic_svr/v1/dynamic_svr/dynamic_history")
  headers.scheme("https")
  val headersFrame = DefaultHttp2HeadersFrame(headers, true)
  streamChannel.writeAndFlush(headersFrame);
  delay(10.seconds().toMillis())
}
