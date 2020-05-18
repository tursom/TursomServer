package cn.tursom.ws

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.utils.bytebuffer.NettyByteBuffer
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.websocketx.*
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import java.net.URI


class WebSocketClient(uri: String, val handler: WebSocketHandler) {
  private val uri: URI = URI.create(uri)
  internal var ch: Channel? = null

  fun open() {
    close()
    val scheme = if (uri.scheme == null) "ws" else uri.scheme
    val host = if (uri.host == null) "127.0.0.1" else uri.host
    val port: Int
    port = if (uri.port == -1) {
      when {
        "ws".equals(scheme, ignoreCase = true) -> 80
        "wss".equals(scheme, ignoreCase = true) -> 443
        else -> -1
      }
    } else {
      uri.port
    }

    if (!"ws".equals(scheme, ignoreCase = true) && !"wss".equals(scheme, ignoreCase = true)) {
      System.err.println("Only WS(S) is supported.")
      return
    }

    val ssl = "wss".equals(scheme, ignoreCase = true)
    val sslCtx = if (ssl) {
      SslContextBuilder.forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE).build()
    } else {
      null
    }

    val handler = WebSocketClientChannelHandler(
      WebSocketClientHandshakerFactory.newHandshaker(
        uri, WebSocketVersion.V13, null, false, DefaultHttpHeaders()
      ), this, handler
    )
    val b = Bootstrap()
    b.group(group)
      .channel(NioSocketChannel::class.java)
      .handler(object : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
          val p = ch.pipeline()
          if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc(), host, port))
          }
          p.addLast(
            HttpClientCodec(),
            HttpObjectAggregator(8192),
            WebSocketClientCompressionHandler.INSTANCE,
            handler
          )
        }
      })
    b.connect(uri.host, port)
    //handler.handshakeFuture().sync()
  }

  fun close() {
    ch?.writeAndFlush(CloseWebSocketFrame())
    ch?.closeFuture()?.sync()
  }

  fun write(text: String): ChannelFuture {
    return ch!!.writeAndFlush(TextWebSocketFrame(text))
  }

  fun write(data: ByteArray): ChannelFuture {
    return ch!!.writeAndFlush(BinaryWebSocketFrame(Unpooled.wrappedBuffer(data)))
  }

  fun write(data: ByteBuffer): ChannelFuture {
    return ch!!.writeAndFlush(
      BinaryWebSocketFrame(
        when (data) {
          is NettyByteBuffer -> data.byteBuf
          else -> Unpooled.wrappedBuffer(data.getBytes())
        }
      )
    )
  }

  fun write(data: ByteBuf): ChannelFuture {
    return ch!!.writeAndFlush(BinaryWebSocketFrame(data))
  }

  fun writeText(data: ByteBuffer): ChannelFuture {
    return ch!!.writeAndFlush(
      TextWebSocketFrame(
        when (data) {
          is NettyByteBuffer -> data.byteBuf
          else -> Unpooled.wrappedBuffer(data.getBytes())
        }
      )
    )
  }

  fun writeText(data: ByteBuf): ChannelFuture {
    return ch!!.writeAndFlush(TextWebSocketFrame(data))
  }

  companion object {
    private val group: EventLoopGroup = NioEventLoopGroup()
  }
}