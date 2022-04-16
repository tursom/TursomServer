package cn.tursom.niothread

import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.concurrent.*

@Suppress("MemberVisibilityCanBePrivate")
class ThreadPoolNioThread(
  val threadName: String = "",
  override val selector: Selector = Selector.open(),
  override val daemon: Boolean = false,
  override val timeout: Long = 3000,
  override val workLoop: (thread: NioThread, key: SelectionKey) -> Unit,
) : NioThread {
  override lateinit var thread: Thread
  val threadPool: ExecutorService = ThreadPoolExecutor(1, 1,
    0L, TimeUnit.MILLISECONDS,
    LinkedBlockingQueue(),
    ThreadFactory {
      val thread = Thread(it)
      this.thread = thread
      thread.isDaemon = daemon
      thread.name = threadName
      thread
    })
  override val closed: Boolean get() = !selector.isOpen

  init {
    threadPool.execute(object : Runnable {
      override fun run() {
        if (selector.isOpen) {
          if (selector.select(timeout) != 0) {
            val keyIter = selector.selectedKeys().iterator()
            while (keyIter.hasNext()) {
              val key = keyIter.next()
              keyIter.remove()
              try {
                workLoop(this@ThreadPoolNioThread, key)
              } catch (e: Exception) {
                e.printStackTrace()
                key.cancel()
                key.channel().close()
              }
            }
          }
          if (!threadPool.isShutdown) threadPool.execute(this)
        }
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

  override fun execute(command: () -> Unit) = threadPool.execute(command)
  override fun <T> call(task: Callable<T>): T = threadPool.submit(task).get()
  override fun <T> submit(task: () -> T): NioThreadTaskFuture<T> = ThreadPoolTaskFuture(threadPool.submit(task))

  override fun close() {
    selector.close()
    threadPool.shutdown()
  }

  class ThreadPoolTaskFuture<T>(val future: Future<T>) : NioThreadTaskFuture<T> {
    override fun get(): T = future.get()
  }

  override fun toString(): String {
    return "ThreadPoolNioThread($threadName)"
  }
}