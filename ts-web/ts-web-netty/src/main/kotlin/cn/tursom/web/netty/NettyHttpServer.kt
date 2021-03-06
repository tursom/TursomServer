package cn.tursom.web.netty

import cn.tursom.core.ws.WebSocketFrameWrapper
import cn.tursom.web.HttpHandler
import cn.tursom.web.HttpServer
import cn.tursom.web.WebSocketHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.ssl.SslHandler
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.LoggerFactory

@Suppress("unused")
class NettyHttpServer(
  override val port: Int,
  handler: HttpHandler<NettyHttpContent, NettyExceptionContent>,
  var bodySize: Int = 512 * 1024,
  autoRun: Boolean = false,
  var webSocketPath: Iterable<Pair<String, WebSocketHandler<NettyWebSocketContent>>> = listOf(),
  var readTimeout: Int = 60,
  var writeTimeout: Int = 0,
  decodeType: NettyHttpDecodeType = if (webSocketPath.iterator()
      .hasNext()
  ) NettyHttpDecodeType.FULL_HTTP else NettyHttpDecodeType.MULTI_PART,
  backlog: Int = 1024,
  val wrapWebSocketFrame: Boolean = false,
  val sslHandlerBuilder: ((allocator: ByteBufAllocator) -> SslHandler)? = null,
) : HttpServer {
  constructor(
    port: Int,
    bodySize: Int = 512 * 1024,
    autoRun: Boolean = false,
    webSocketPath: Iterable<Pair<String, WebSocketHandler<NettyWebSocketContent>>> = listOf(),
    readTimeout: Int = 60,
    writeTimeout: Int = 0,
    decodeType: NettyHttpDecodeType = if (webSocketPath.iterator().hasNext()) {
      NettyHttpDecodeType.FULL_HTTP
    } else {
      NettyHttpDecodeType.MULTI_PART
    },
    backlog: Int = 1024,
    wrapWebSocketFrame: Boolean = false,
    sslHandlerBuilder: ((allocator: ByteBufAllocator) -> SslHandler)? = null,
    handler: (content: NettyHttpContent) -> Unit,
  ) : this(
    port,
    object : HttpHandler<NettyHttpContent, NettyExceptionContent> {
      override fun handle(content: NettyHttpContent) = handler(content)
    },
    bodySize,
    autoRun,
    webSocketPath,
    readTimeout,
    writeTimeout,
    decodeType,
    backlog,
    wrapWebSocketFrame,
    sslHandlerBuilder
  )

  var decodeType: NettyHttpDecodeType = decodeType
    set(value) {
      if (value != field) {
        field = value
        updateHandler()
      }
    }
  var handler: HttpHandler<NettyHttpContent, NettyExceptionContent> = handler
    set(value) {
      field = value
      updateHandler()
    }

  private var httpHandler = updateHandler()
  private val group = NioEventLoopGroup()
  private val b = ServerBootstrap().group(group)
    .channel(NioServerSocketChannel::class.java)
    .childHandler(object : ChannelInitializer<SocketChannel>() {
      override fun initChannel(ch: SocketChannel) {
        val pipeline = ch.pipeline()

        val sslHandler = sslHandlerBuilder?.invoke(ch.alloc())
        if (sslHandler != null) {
          pipeline.addLast(sslHandler)
        }

        if (readTimeout > 0) {
          pipeline.addLast(ReadTimeoutHandler(readTimeout))
        }
        if (writeTimeout > 0) {
          pipeline.addLast(WriteTimeoutHandler(writeTimeout))
        }
        pipeline.addLast("codec", HttpServerCodec())
        if (this@NettyHttpServer.decodeType == NettyHttpDecodeType.FULL_HTTP) {
          pipeline.addLast("aggregator", HttpObjectAggregator(bodySize))
        }
        pipeline.addLast("http-chunked", ChunkedWriteHandler())
        this@NettyHttpServer.webSocketPath.forEach { (webSocketPath, handler) ->
          pipeline.addLast("ws-$webSocketPath", WebSocketServerProtocolHandler(webSocketPath))
          pipeline.addLast("wsHandler-$webSocketPath", NettyWebSocketHandler(ch, handler))
        }
        if (wrapWebSocketFrame && webSocketPath.iterator().hasNext()) {
          pipeline.addLast(WebSocketFrameWrapper)
        }
        pipeline.addLast("handle", httpHandler)
      }
    })
    .option(ChannelOption.SO_BACKLOG, backlog) // determining the number of connections queued
    .option(ChannelOption.SO_REUSEADDR, true)
    .childOption(ChannelOption.SO_KEEPALIVE, java.lang.Boolean.TRUE)
  private var future: ChannelFuture? = null

  init {
    if (autoRun) run()
  }

  override fun run() {
    if (future != null) return
    future = b.bind(port)
    log?.warn("NettyHttpServer started on port {}", port)
    log?.info(
      "try http{}://localhost:{}/",
      if (sslHandlerBuilder == null) "" else "s",
      when {
        sslHandlerBuilder == null && port == 80 -> ""
        sslHandlerBuilder != null && port == 443 -> ""
        else -> port
      }
    )
    future!!.sync()
  }

  override fun close() {
    log?.warn("NettyHttpServer({}) closed", port)
    future?.cancel(false)
    future?.channel()?.close()
  }

  private fun updateHandler(): SimpleChannelInboundHandler<out HttpObject> {
    httpHandler = when (decodeType) {
      NettyHttpDecodeType.FULL_HTTP -> NettyHttpHandler(handler)
      NettyHttpDecodeType.MULTI_PART -> NettyHttpObjectHandler(handler)
    }
    return httpHandler
  }

  companion object {
    private val log = try {
      LoggerFactory.getLogger(NettyHttpServer::class.java)
    } catch (e: Throwable) {
      null
    }
  }
}