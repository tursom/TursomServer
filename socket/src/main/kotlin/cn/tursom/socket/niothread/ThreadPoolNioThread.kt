package cn.tursom.socket.niothread

import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.concurrent.*

@Suppress("MemberVisibilityCanBePrivate")
class ThreadPoolNioThread(
    val threadName: String = "",
    override val selector: Selector = Selector.open(),
    override val isDaemon: Boolean = false,
    override val workLoop: (thread: INioThread) -> Unit
) : INioThread {
  override lateinit var thread: Thread
  val threadPool: ExecutorService = ThreadPoolExecutor(1, 1,
      0L, TimeUnit.MILLISECONDS,
      LinkedBlockingQueue<Runnable>(),
      ThreadFactory {
        val thread = Thread(it)
        this.thread = thread
        thread.isDaemon = isDaemon
        thread.name = threadName
        thread
      })
  override var closed: Boolean = false

  init {
    threadPool.execute(object : Runnable {
      override fun run() {
        workLoop(this@ThreadPoolNioThread)
        if (!threadPool.isShutdown) threadPool.execute(this)
      }
    })
  }

  override fun wakeup() {
    if (Thread.currentThread() != thread) {
      selector.wakeup()
    }
  }

  override fun register(channel: SelectableChannel, ops: Int, onComplete: (key: SelectionKey) -> Unit) {
    if (Thread.currentThread() == thread) {
      onComplete(channel.register(selector, ops))
    } else {
      threadPool.execute { register(channel, ops, onComplete) }
      wakeup()
    }
  }

  override fun execute(command: Runnable) = threadPool.execute(command)
  override fun <T> call(task: Callable<T>): T = threadPool.submit(task).get()
  override fun <T> submit(task: Callable<T>): NioThreadFuture<T> = ThreadPoolFuture(threadPool.submit(task))

  override fun close() {
    closed = true
    threadPool.shutdown()
  }

  class ThreadPoolFuture<T>(val future: Future<T>) : NioThreadFuture<T> {
    override fun get(): T = future.get()
  }

  override fun toString(): String {
    return "SingleThreadNioThread($threadName)"
  }
}