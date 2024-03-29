package cn.tursom.niothread

import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.concurrent.LinkedTransferQueue

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class WorkerLoopNioThread(
  val threadName: String = "nioLoopThread",
  override var selector: Selector = Selector.open(),
  override val daemon: Boolean = false,
  override val timeout: Long = 3000,
  override val workLoop: (thread: NioThread, key: SelectionKey) -> Unit,
) : NioThread {
  override val closed: Boolean get() = !selector.isOpen

  val waitQueue = LinkedTransferQueue<() -> Unit>()
  //val taskQueue = LinkedBlockingDeque<Future<Any?>>()

  override val thread = Thread {
    while (selector.isOpen) {
      try {
        if (selector.select(timeout) != 0) {
          val keyIter = selector.selectedKeys().iterator()
          while (keyIter.hasNext()) {
            val key = keyIter.next()
            keyIter.remove()
            try {
              workLoop(this, key)
            } catch (e: Exception) {
              e.printStackTrace()
              key.cancel()
              key.channel().close()
            }
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      while (true) try {
        (waitQueue.take() ?: break)()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  init {
    thread.name = threadName
    thread.isDaemon = daemon
    thread.start()
  }

  override fun execute(command: () -> Unit) {
    waitQueue.add(command)
  }

  override fun <T> submit(task: () -> T): NioThreadTaskFuture<T> {
    val f = Future<T>()
    waitQueue.add {
      try {
        f.resume(task())
      } catch (e: Throwable) {
        f.resumeWithException(e)
      }
    }
    return f
  }

  override fun close() {
    execute {
      selector.close()
    }
    wakeup()
  }

  override fun wakeup() {
    if (Thread.currentThread() != thread) {
      selector.wakeup()
    }
  }

  override fun toString(): String {
    return "WorkerLoopNioThread(threadName='$threadName', selector=$selector, daemon=$daemon, timeout=$timeout, workLoop=$workLoop)"
  }

  class Future<T> : NioThreadTaskFuture<T> {
    private val lock = Object()
    private var exception: Throwable? = null
    private var result: T? = null

    @Throws(Throwable::class)
    override fun get(): T {
      val result = this.result
      return when {
        exception != null -> throw exception as Throwable
        result != null -> result
        else -> synchronized(lock) {
          lock.wait()
          val exception = this.exception
          if (exception != null) {
            throw exception
          } else {
            this.result!!
          }
        }
      }
    }

    fun resume(value: T) {
      result = value
      synchronized(lock) {
        lock.notifyAll()
      }
    }

    fun resumeWithException(e: Throwable) {
      exception = e
      synchronized(lock) {
        lock.notifyAll()
      }
    }
  }
}