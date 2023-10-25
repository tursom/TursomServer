package cn.tursom.core.ws

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.impl.NettyByteBuffer
import cn.tursom.core.util.ShutdownHook
import cn.tursom.core.util.notifyAll
import cn.tursom.core.util.uncheckedCast
import cn.tursom.core.util.wait
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.*
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
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


@Suppress("unused", "MemberVisibilityCanBePrivate")
open class WebSocketClient<in T : WebSocketClient<T, H>, H : WebSocketHandler<T, H>>(
  url: String,
  open val handler: H,
  val autoWrap: Boolean = true,
  val log: Boolean = false,
  val compressed: Boolean = true,
  val maxContextLength: Int = 4096,
  private val headers: Map<String, String>? = null,
  private val handshakeUri: URI? = null,
  val autoRelease: Boolean = true,
  var initChannel: ((ch: SocketChannel) -> Unit)? = null,
) {
  companion object {
    private val threadId = AtomicInteger()
    private val group: EventLoopGroup = NioEventLoopGroup(0, ThreadFactory {
      val thread = Thread(it, "WebSocketClient-${threadId.incrementAndGet()}")
      thread.isDaemon = true
      thread
    })
  }

  private val uri: URI = URI.create(url)
  var ch: Channel? = null
    internal set
  var closed: Boolean = false
    private set
  private val onOpenLock = AtomicBoolean()
  val onOpen get() = onOpenLock.get()

  private val hook = ShutdownHook.addSoftShutdownHook {
    close()
  }

  init {
    uncheckedCast<T>()
  }

  fun open(): ChannelFuture? {
    if (!onOpenLock.compareAndSet(false, true)) {
      return null
    }
    try {
      close()
      return open1()
    } finally {
      onOpenLock.set(false)
    }
  }

  private fun open1(): ChannelFuture? {
    val scheme = if (uri.scheme == null) "ws" else uri.scheme
    val host = if (uri.host == null) "127.0.0.1" else uri.host
    val port = if (uri.port == -1) {
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
      return null
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
    val handshakerAdapter = WebSocketClientHandshakerAdapter(
      WebSocketClientHandshakerFactory.newHandshaker(
        handshakeUri ?: uri, WebSocketVersion.V13, null, true, httpHeaders
      ), uncheckedCast(), handler
    )
    val handler = WebSocketClientChannelHandler(uncheckedCast(), handler, autoRelease)
    val bootstrap = Bootstrap()
      .group(group)
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
            addLast(handler)
            if (autoWrap) {
              addLast(WebSocketFrameWrapper)
            }
          }
          initChannel?.invoke(ch)
        }
      })
    return bootstrap.connect(uri.host, port)
  }

  fun close(reasonText: String? = null): ChannelFuture? {
    if (reasonText == null) {
      ch?.writeAndFlush(CloseWebSocketFrame())
    } else {
      ch?.writeAndFlush(CloseWebSocketFrame(WebSocketCloseStatus.NORMAL_CLOSURE, reasonText))
    }?.addListener(ChannelFutureListener.CLOSE)
    return ch?.closeFuture()
  }

  fun write(text: String): ChannelFuture {
    return ch!!.writeAndFlush(TextWebSocketFrame(text))
  }

  fun write(data: ByteArray): ChannelFuture {
    return ch!!.writeAndFlush(BinaryWebSocketFrame(Unpooled.wrappedBuffer(data)))
  }

  fun write(data: ByteBuffer): ChannelFuture {
    return ch!!.writeAndFlush(
      BinaryWebSocketFrame(NettyByteBuffer.toByteBuf(data))
    )
  }

  fun write(data: ByteBuf): ChannelFuture {
    return ch!!.writeAndFlush(BinaryWebSocketFrame(data))
  }

  fun writeText(data: ByteBuffer): ChannelFuture {
    return ch!!.writeAndFlush(
      TextWebSocketFrame(NettyByteBuffer.toByteBuf(data))
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
      PingWebSocketFrame(NettyByteBuffer.toByteBuf(data))
    )
  }

  fun ping(data: ByteBuf): ChannelFuture {
    return ch!!.writeAndFlush(PingWebSocketFrame(data))
  }

  fun pong(data: ByteArray): ChannelFuture {
    return ch!!.writeAndFlush(PongWebSocketFrame(Unpooled.wrappedBuffer(data)))
  }

  fun pong(data: ByteBuffer): ChannelFuture {
    return ch!!.writeAndFlush(PongWebSocketFrame(NettyByteBuffer.toByteBuf(data)))
  }

  fun pong(data: ByteBuf): ChannelFuture {
    return ch!!.writeAndFlush(PongWebSocketFrame(data))
  }

  open fun onOpen() {
    closed = false
  }

  open fun onClose() {
    synchronized(this) {
      closed = true
      notifyAll()
    }
  }

  fun waitClose() {
    if (!closed) synchronized(this) {
      if (closed) {
        return
      }
      wait()
    }
  }
}
