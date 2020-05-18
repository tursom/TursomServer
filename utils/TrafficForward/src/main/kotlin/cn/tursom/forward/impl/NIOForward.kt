package cn.tursom.forward.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.buffer.read
import cn.tursom.core.buffer.write
import cn.tursom.core.pool.DirectMemoryPool
import cn.tursom.core.pool.ExpandableMemoryPool
import cn.tursom.core.pool.MemoryPool
import cn.tursom.core.timer.WheelTimer
import cn.tursom.core.unaryPlus
import cn.tursom.forward.Forward
import cn.tursom.niothread.NioProtocol
import cn.tursom.niothread.NioThread
import cn.tursom.niothread.WorkerLoopNioThread
import cn.tursom.niothread.loophandler.WorkerLoopHandler
import io.netty.util.internal.logging.InternalLogger
import io.netty.util.internal.logging.Slf4JLoggerFactory
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.channels.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * only support UDP(DatagramChannel) and TCP(SocketChannel)
 */
open class NIOForward(
  host: SocketAddress,
  private val channel: SelectableChannel = udp(),
  val timeout: Long = 3,
  final override var forward: Forward? = null
) : Forward {
  private lateinit var key: SelectionKey
  private var timeoutTask = timer.exec(timeout * 1000) {
    close()
  }

  constructor(
    host: String,
    port: Int,
    channel: SelectableChannel = udp(),
    timeout: Long = 3,
    forward: Forward? = null
  ) : this(InetSocketAddress(host, port), channel, timeout, forward)

  init {
    when (channel) {
      is SocketChannel, is DatagramChannel -> {
      }
      else -> throw UnsupportedOperationException()
    }
    @Suppress("LeakingThis")
    forward?.forward = this
    channel.configureBlocking(false)
    when (channel) {
      is SocketChannel -> channel.connect(host)
      is DatagramChannel -> channel.connect(host)
    }
    val latch = CountDownLatch(1)
    nioThread.register(
      channel,
      if (channel is DatagramChannel) SelectionKey.OP_READ else SelectionKey.OP_CONNECT
    ) { key ->
      this.key = key
      if (channel is DatagramChannel) {
        latch.countDown()
        key.attach(this)
      } else {
        key.attach(this to latch)
      }
    }
    latch.await(3, TimeUnit.SECONDS)
  }

  private fun resetTimeout() {
    timeoutTask.cancel()
    timeoutTask = timer.exec(timeout * 1000) {
      close()
    }
  }

  override fun write(buffer: ByteBuffer) {
    log.debug("recv msg from agent {}", +{ buffer.toString(buffer.readable) })
    resetTimeout()
    if (channel is WritableByteChannel) {
      channel.write(buffer)
      buffer.close()
    } else {
      buffer.close()
      throw UnsupportedOperationException()
    }
  }

  override fun close() {
    nioThread.execute {
      key.cancel()
    }
  }

  open fun recvMsg(msg: ByteBuffer) {
    log.debug("connected from tcp {}", +{ msg.toString(msg.readable) })
    resetTimeout()
    forward?.write(msg)
  }

  @Suppress("unused")
  companion object {
    fun tcp(): SocketChannel = SocketChannel.open()
    fun udp(): DatagramChannel = DatagramChannel.open()
    private val timer = WheelTimer.timer
    private val log: InternalLogger = Slf4JLoggerFactory.getInstance(NIOForward::class.java)
    private val memoryPool: MemoryPool = ExpandableMemoryPool(128) { DirectMemoryPool(1024, 16) }

    @Suppress("UNCHECKED_CAST")
    private val nioThread: NioThread = WorkerLoopNioThread(
      "NIOForwardLooper",
      workLoop = WorkerLoopHandler(object : NioProtocol {
        override fun handleConnect(key: SelectionKey, nioThread: NioThread) {
          key.interestOps(SelectionKey.OP_READ)
          val (forward, latch) = (key.attachment() as Pair<NIOForward, CountDownLatch>)
          latch.countDown()
          key.attach(forward)
        }

        override fun handleRead(key: SelectionKey, nioThread: NioThread) {
          val channel = key.channel()
          val forward = key.attachment() as NIOForward
          val buffer = memoryPool.get()
          if (channel is ReadableByteChannel) {
            channel.read(buffer)
          }
          log.debug("recv msg from {}: {}", +{
            when (channel) {
              is SocketChannel -> channel.remoteAddress
              is DatagramChannel -> channel.remoteAddress
              else -> null
            }
          }, +{
            buffer.toString(buffer.readable)
          })
          forward.recvMsg(buffer)
          buffer.close()
        }

        override fun handleWrite(key: SelectionKey, nioThread: NioThread) {}

        override fun exceptionCause(key: SelectionKey, nioThread: NioThread, e: Throwable) {
          val channel = key.channel()
          log.error("exception caused on handler msg, address: {}", +{
            when (channel) {
              is SocketChannel -> channel.remoteAddress
              is DatagramChannel -> channel.remoteAddress
              else -> null
            }
          }, e)
          val forward = key.attachment() as NIOForward
          forward.close()
          forward.forward?.close()
        }
      })
    )
  }
}

