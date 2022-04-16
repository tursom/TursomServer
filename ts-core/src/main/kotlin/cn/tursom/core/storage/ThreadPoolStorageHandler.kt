package cn.tursom.core.storage

import java.util.concurrent.Executor

/**
 * 使用线程池进行写入的存储处理器
 */
class ThreadPoolStorageHandler<T>(
  // 任务执行线程池
  private val executor: Executor,
  // 数据批量写入处理器
  private val writeHandler: (obj: T) -> Unit,
) : StorageHandler<T> {
  /**
   * 向缓冲中添加一个写入对象
   */
  override fun add(obj: T) {
    executor.execute {
      writeHandler(obj)
    }
  }
}
