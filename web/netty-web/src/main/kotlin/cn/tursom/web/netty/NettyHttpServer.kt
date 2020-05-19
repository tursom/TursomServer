package cn.tursom.web.netty

import cn.tursom.web.HttpHandler
import cn.tursom.web.HttpServer
import cn.tursom.web.WebSocketHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
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
  bodySize: Int = 512 * 1024,
  autoRun: Boolean = false,
  webSocketPath: Iterable<Pair<String, WebSocketHandler<NettyWebSocketContext>>> = listOf(),
  readTimeout: Int? = null,
  writeTimeout: Int? = null
) : HttpServer {
  constructor(
    port: Int,
    bodySize: Int = 512 * 1024,
    autoRun: Boolean = false,
    webSocketPath: Iterable<Pair<String, WebSocketHandler<NettyWebSocketContext>>> = listOf(),
    readTimeout: Int? = null,
    writeTimeout: Int? = null,
    handler: (content: NettyHttpContent) -> Unit
  ) : this(
    port,
    object : HttpHandler<NettyHttpContent, NettyExceptionContent> {
      override fun handle(content: NettyHttpContent) = handler(content)
    },
    bodySize, autoRun, webSocketPath, readTimeout, writeTimeout
  )

  val httpHandler = NettyHttpHandler(handler)
  private val group = NioEventLoopGroup()
  private val b = ServerBootstrap().group(group)
    .channel(NioServerSocketChannel::class.java)
    .childHandler(object : ChannelInitializer<SocketChannel>() {
      override fun initChannel(ch: SocketChannel) {
        ch.pipeline()
          .apply {
            if (readTimeout != null) addLast(ReadTimeoutHandler(readTimeout))
          }
          .apply {
            if (writeTimeout != null) addLast(WriteTimeoutHandler(writeTimeout))
          }
          .addLast("codec", HttpServerCodec())
          .addLast("aggregator", HttpObjectAggregator(bodySize))
          .addLast("http-chunked", ChunkedWriteHandler())
          .apply {
            webSocketPath.forEach { (webSocketPath, handler) ->
              addLast("ws-$webSocketPath", WebSocketServerProtocolHandler(webSocketPath))
              addLast("wsHandler-$webSocketPath", NettyWebSocketHandler(ch, handler))
            }
          }
          .addLast("handle", httpHandler)
      }
    })
    .option(ChannelOption.SO_BACKLOG, 1024) // determining the number of connections queued
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

  companion object {
    private val log = try {
      LoggerFactory.getLogger(NettyHttpServer::class.java)
    } catch (e: Throwable) {
      null
    }
  }
}