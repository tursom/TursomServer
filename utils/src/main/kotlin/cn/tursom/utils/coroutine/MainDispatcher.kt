package cn.tursom.utils.coroutine

import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.io.Closeable
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

object MainDispatcher : MainCoroutineDispatcher(), Closeable {
  private val dispatcher = Executors.newSingleThreadExecutor {
    thread(start = false, block = it::run, name = "mainDispatcher", isDaemon = false)
  }.asCoroutineDispatcher()

  fun init() {
    val mainDispatcherLoader = Class.forName("kotlinx.coroutines.internal.MainDispatcherLoader")
    val dispatcher = mainDispatcherLoader.getDeclaredField("dispatcher")
    dispatcher.isAccessible = true
    val mf: Field = Field::class.java.getDeclaredField("modifiers")
    mf.isAccessible = true
    mf.setInt(dispatcher, dispatcher.modifiers and Modifier.FINAL.inv())
    dispatcher.set(null, this)
  }

  override val immediate: MainCoroutineDispatcher get() = this

  override fun dispatch(context: CoroutineContext, block: Runnable) {
    dispatcher.dispatch(context, block)
  }

  override fun close() {
    dispatcher.close()
  }
}