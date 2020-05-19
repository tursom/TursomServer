package cn.tursom.web.netty

import cn.tursom.web.HttpHandler
import cn.tursom.web.HttpServer
import cn.tursom.web.WebSocketHandler
import io.netty.bootstrap.ServerBootstrap
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
  var readTimeout: Int? = null,
  var writeTimeout: Int? = null,
  decodeType: NettyHttpDecodeType = NettyHttpDecodeType.MULTI_PART,
  backlog: Int = 1024
) : HttpServer {
  constructor(
    port: Int,
    bodySize: Int = 512 * 1024,
    autoRun: Boolean = false,
    webSocketPath: Iterable<Pair<String, WebSocketHandler<NettyWebSocketContent>>> = listOf(),
    readTimeout: Int? = null,
    writeTimeout: Int? = null,
    decodeType: NettyHttpDecodeType = NettyHttpDecodeType.MULTI_PART,
    handler: (content: NettyHttpContent) -> Unit
  ) : this(
    port,
    object : HttpHandler<NettyHttpContent, NettyExceptionContent> {
      override fun handle(content: NettyHttpContent) = handler(content)
    },
    bodySize, autoRun, webSocketPath, readTimeout, writeTimeout, decodeType
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
        readTimeout?.let {
          pipeline.addLast(ReadTimeoutHandler(it))
        }
        writeTimeout?.let {
          pipeline.addLast(WriteTimeoutHandler(it))
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
        pipeline.addLast("handle", httpHandler)
      }
    })
    .option(ChannelOption.SO_BACKLOG, backlog) // determining the number of connections queued
    .option(ChannelOption.SO_REUSEADDR, true)
    .childOption(ChannelOption.SO_KEEPALIVE, java.lang.Boolean.TRUE)
  private val future: ChannelFuture = b.bind(port)

  init {
    if (autoRun) run()
  }

  override fun run() {
    log?.warn("NettyHttpServer started on port {}", port)
    log?.info("try http://localhost:{}/", port)
    future.sync()
  }

  override fun close() {
    log?.warn("NettyHttpServer({}) closed", port)
    future.cancel(false)
    future.channel().close()
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