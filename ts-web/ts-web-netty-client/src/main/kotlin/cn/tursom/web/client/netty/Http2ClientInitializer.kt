package cn.tursom.web.client.netty

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http2.*
import io.netty.handler.logging.LogLevel
import io.netty.handler.ssl.ApplicationProtocolNames
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler
import io.netty.handler.ssl.SslContext
import java.net.InetSocketAddress

/**
 * Configures the client pipeline to support HTTP/2 frames.
 */
class Http2ClientInitializer(
  private val sslCtx: SslContext?,
  private val maxContentLength: Int,
) :
  ChannelInitializer<SocketChannel>() {
  @Throws(Exception::class)
  public override fun initChannel(ch: SocketChannel) {
    val connection = DefaultHttp2Connection(false)
    val connectionHandler = HttpToHttp2ConnectionHandlerBuilder()
      .frameListener(DelegatingDecompressorFrameListener(
        connection,
        InboundHttp2ToHttpAdapterBuilder(connection)
          .maxContentLength(maxContentLength)
          .propagateSettings(true)
          .build()))
      .frameLogger(logger)
      .connection(connection)
      .build()
    if (sslCtx != null) {
      configureSsl(ch, connectionHandler)
    } else {
      configureClearText(ch, connectionHandler)
    }
  }


  protected fun configureEndOfPipeline(pipeline: ChannelPipeline) {
  }

  /**
   * Configure the pipeline for TLS NPN negotiation to HTTP/2.
   */
  private fun configureSsl(
    ch: SocketChannel,
    connectionHandler: HttpToHttp2ConnectionHandler,
  ) {
    val pipeline = ch.pipeline()
    // Specify Host in SSLContext New Handler to add TLS SNI Extension
    pipeline.addLast(sslCtx!!.newHandler(ch.alloc(), "Http2Client.HOST", 0))
    // We must wait for the handshake to finish and the protocol to be negotiated before configuring
    // the HTTP/2 components of the pipeline.
    pipeline.addLast(object : ApplicationProtocolNegotiationHandler("") {
      override fun configurePipeline(ctx: ChannelHandlerContext, protocol: String) {
        if (ApplicationProtocolNames.HTTP_2 == protocol) {
          val p = ctx.pipeline()
          p.addLast(connectionHandler)
          configureEndOfPipeline(p)
          return
        }
        ctx.close()
        throw IllegalStateException("unknown protocol: $protocol")
      }
    })
  }

  /**
   * Configure the pipeline for a cleartext upgrade from HTTP to HTTP/2.
   */
  private fun configureClearText(ch: SocketChannel, connectionHandler: HttpToHttp2ConnectionHandler) {
    val sourceCodec = HttpClientCodec()
    val upgradeCodec = Http2ClientUpgradeCodec(connectionHandler)
    val upgradeHandler = HttpClientUpgradeHandler(sourceCodec, upgradeCodec, 65536)
    ch.pipeline().addLast(sourceCodec,
      upgradeHandler,
      UpgradeRequestHandler(),
      UserEventLogger())
  }

  /**
   * A handler that triggers the cleartext upgrade to HTTP/2 by sending an initial HTTP request.
   */
  private inner class UpgradeRequestHandler : ChannelInboundHandlerAdapter() {
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
      val upgradeRequest = DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/", Unpooled.EMPTY_BUFFER)

      // Set HOST header as the remote peer may require it.
      val remote = ctx.channel().remoteAddress() as InetSocketAddress
      var hostString = remote.hostString
      if (hostString == null) {
        hostString = remote.address.hostAddress
      }
      upgradeRequest.headers()[HttpHeaderNames.HOST] = hostString + ':' + remote.port
      ctx.writeAndFlush(upgradeRequest)
      ctx.fireChannelActive()

      // Done with this handler, remove it from the pipeline.
      ctx.pipeline().remove(this)
      configureEndOfPipeline(ctx.pipeline())
    }
  }

  /**
   * Class that logs any User Events triggered on this channel.
   */
  private class UserEventLogger : ChannelInboundHandlerAdapter() {
    @Throws(Exception::class)
    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
      println("User Event Triggered: $evt")
      ctx.fireUserEventTriggered(evt)
    }
  }

  companion object {
    val logger = Http2FrameLogger(LogLevel.INFO,
      Http2ClientInitializer::class.java)
  }
}