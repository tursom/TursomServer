package cn.tursom.ws

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.NettyByteBuffer
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
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import java.net.URI


@Suppress("unused")
class WebSocketClient(
  url: String,
  val handler: WebSocketHandler,
  val autoWrap: Boolean = true,
  val log: Boolean = false,
  val compressed: Boolean = true,
  val maxContextLength: Int = 4096,
  private val headers: Map<String, String>? = null,
  private val handshakerUri: URI? = null,
) {
  private val uri: URI = URI.create(url)
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
    val httpHeaders = DefaultHttpHeaders()
    headers?.forEach { (k, v) ->
      httpHeaders[k] = v
    }
    val handshakerAdapter = WebSocketClientHandshakerAdapter(WebSocketClientHandshakerFactory.newHandshaker(
      handshakerUri ?: uri, WebSocketVersion.V13, null, true, httpHeaders
    ), this, handler)
    val handler = WebSocketClientChannelHandler(this, handler)
    val bootstrap = Bootstrap()
    bootstrap.group(group)
      .channel(NioSocketChannel::class.java)
      .handler(object : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
          ch.pipeline().apply {
            if (log) {
              addLast(LoggingHandler())
            }
            if (sslCtx != null) {
              addLast(sslCtx.newHandler(ch.alloc(), host, port))
            }
            addLast(HttpClientCodec())
            addLast(HttpObjectAggregator(maxContextLength))
            if (compressed) {
              addLast(WebSocketClientCompressionHandler.INSTANCE)
            }
            addLast(handshakerAdapter)
            //if (log) {
            //  addLast(LoggingHandler())
            //}
            addLast(handler)
            if (autoWrap) {
              addLast(WebSocketFrameWrapper)
            }
          }
        }
      })
    bootstrap.connect(uri.host, port)
    //handler.handshakeFuture().sync()
  }

  fun close(reasonText: String? = null) {
    if (reasonText == null) {
      ch?.writeAndFlush(CloseWebSocketFrame())
    } else {
      ch?.writeAndFlush(CloseWebSocketFrame(WebSocketCloseStatus.NORMAL_CLOSURE, reasonText))
    }
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

  fun ping(data: ByteArray): ChannelFuture {
    return ch!!.writeAndFlush(PingWebSocketFrame(Unpooled.wrappedBuffer(data)))
  }

  fun ping(data: ByteBuffer): ChannelFuture {
    return ch!!.writeAndFlush(
      PingWebSocketFrame(
        when (data) {
          is NettyByteBuffer -> data.byteBuf
          else -> Unpooled.wrappedBuffer(data.getBytes())
        }
      )
    )
  }

  fun ping(data: ByteBuf): ChannelFuture {
    return ch!!.writeAndFlush(PingWebSocketFrame(data))
  }

  fun pong(data: ByteArray): ChannelFuture {
    return ch!!.writeAndFlush(PongWebSocketFrame(Unpooled.wrappedBuffer(data)))
  }

  fun pong(data: ByteBuffer): ChannelFuture {
    return ch!!.writeAndFlush(
      PongWebSocketFrame(
        when (data) {
          is NettyByteBuffer -> data.byteBuf
          else -> Unpooled.wrappedBuffer(data.getBytes())
        }
      )
    )
  }

  fun pong(data: ByteBuf): ChannelFuture {
    return ch!!.writeAndFlush(PongWebSocketFrame(data))
  }

  companion object {
    private val group: EventLoopGroup = NioEventLoopGroup()
  }
}