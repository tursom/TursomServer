package cn.tursom.core.coroutine

import cn.tursom.core.cast
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.io.Closeable
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

object MainDispatcher : MainCoroutineDispatcher(), Closeable {
  private val loaded = AtomicBoolean(false)
  private val dispatcher: ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor {
    thread(start = false, block = it::run, name = "MainDispatcher", isDaemon = false)
  }.asCoroutineDispatcher()
  private var oldDispatcher: MainCoroutineDispatcher? = null
  private val mainDispatcherLoader: Class<*> = Class.forName("kotlinx.coroutines.internal.MainDispatcherLoader")
  private val dispatcherField: Field = mainDispatcherLoader.getDeclaredField("dispatcher").also { dispatcher ->
    dispatcher.isAccessible = true
    val mf: Field = Field::class.java.getDeclaredField("modifiers")
    mf.isAccessible = true
    mf.setInt(dispatcher, dispatcher.modifiers and Modifier.FINAL.inv())
  }

  fun init() {
    if (loaded.compareAndSet(false, true)) {
      oldDispatcher = dispatcherField.get(null).cast()
      dispatcherField.set(null, this)
    }
  }

  fun resume() {
    if (loaded.compareAndSet(true, false) && oldDispatcher != null) {
      dispatcherField.set(null, oldDispatcher)
    }
  }

  override val immediate: MainCoroutineDispatcher get() = this

  override fun dispatch(context: CoroutineContext, block: Runnable) {
    dispatcher.dispatch(context, block)
  }

  override fun close() {
    dispatcher.close()
  }
}