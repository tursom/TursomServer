package cn.tursom.utils.storage

import cn.tursom.core.storage.StorageHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

class AsyncBufferedStorageHandler<T>(
  // 最小缓冲时间
  private val minBufTime: Long = 500,
  private val singleThreadWrite: Boolean = true,
  // 数据批量写入处理器
  private val writeHandler: suspend (list: Collection<T>) -> Unit
) : StorageHandler<T> {
  private val onWrite = AtomicBoolean(false)

  @Volatile
  private var msgList = ConcurrentLinkedQueue<T>()

  private val write = object {
    suspend operator fun invoke() {
      val list = msgList
      delay(minBufTime)
      msgList = ConcurrentLinkedQueue()
      // 可能还有未释放 msgList 对象的线程，要稍微等待一下
      delay(1)
      if (singleThreadWrite) {
        try {
          writeHandler(list)
        } finally {
          if (msgList.isNotEmpty()) {
            val write = this
            GlobalScope.launch { write() }
          } else {
            onWrite.set(false)
          }
        }
      } else {
        onWrite.set(false)
        writeHandler(list)
      }
    }
  }

  /**
   * 向缓冲中添加一个写入对象
   */
  override fun add(obj: T) {
    msgList.add(obj)
    if (onWrite.compareAndSet(false, true)) {
      GlobalScope.launch {
        write()
      }
    }
  }
}