package cn.tursom.niothread

import java.io.Closeable
import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.concurrent.Callable
import java.util.concurrent.Executor

/**
 * 一个 nio 工作线程
 * 一个线程对应一个 Selector （选择器）
 */
interface NioThread : Closeable, Executor {
  val selector: Selector
  val closed: Boolean
  val timeout: Long
  val workLoop: (thread: NioThread, key: SelectionKey) -> Unit
  val thread: Thread
  val daemon: Boolean

  fun wakeup() {
    if (Thread.currentThread() != thread) selector.wakeup()
  }

  /**
   * 将通道注册到线程对应的选择器上
   */
  fun register(channel: SelectableChannel, ops: Int, onComplete: (key: SelectionKey) -> Unit) {
    if (Thread.currentThread() == thread) {
      val key = channel.register(selector, ops)
      onComplete(key)
    } else {
      execute {
        val key = channel.register(selector, ops)
        onComplete(key)
      }
      wakeup()
    }
  }

  override fun execute(command: Runnable) = execute(command::run)
  fun execute(command: () -> Unit)

  fun <T> call(task: Callable<T>): T {
    return submit(task).get()
  }

  fun <T> call(task: () -> T): T {
    return call(Callable { task() })
  }

  fun <T> submit(task: Callable<T>): NioThreadTaskFuture<T> = submit(task::call)
  fun <T> submit(task: () -> T): NioThreadTaskFuture<T>
}

