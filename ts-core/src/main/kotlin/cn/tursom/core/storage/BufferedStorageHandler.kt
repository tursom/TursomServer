package cn.tursom.core.storage

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 缓冲数据集中写入
 * 自动选择合适时间执行批量写入操作
 * 工作流程：
 *  - 插入：
 *    A添加对象写入->加入缓冲队列->第一个加入缓冲队列的向线程池添加写缓冲任务
 *  - 写缓冲任务:
 *    保存现有列表->等待一定时间，使后续请求能够继续写入缓冲->计时到时刷新缓冲并写入旧缓冲
 *  通过 AtomicBoolean onWrite 控制同一时刻只有一个线程在等待缓冲
 */
class BufferedStorageHandler<T>(
  // 任务执行线程池
  private val executor: Executor,
  // 最小缓冲时间
  private val minBufTime: Long = 500,
  private val singleThreadWrite: Boolean = true,
  // 数据批量写入处理器
  private val writeHandler: (list: Collection<T>) -> Unit,
) : StorageHandler<T> {
  private val onWrite = AtomicBoolean(false)

  @Volatile
  private var msgList = ConcurrentLinkedQueue<T>()

  private val write = object : Runnable {
    override fun run() {
      val list = msgList
      Thread.sleep(minBufTime)
      msgList = ConcurrentLinkedQueue()
      // 可能还有未释放 msgList 对象的线程，要稍微等待一下
      Thread.sleep(1)
      if (singleThreadWrite) {
        try {
          writeHandler(list)
        } finally {
          if (msgList.isNotEmpty()) {
            executor.execute(this)
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
      executor.execute(write)
    }
  }
}