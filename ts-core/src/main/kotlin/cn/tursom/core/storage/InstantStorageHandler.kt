package cn.tursom.core.storage

/**
 * 即时写入存储处理器
 */
class InstantStorageHandler<T>(
  // 数据批量写入处理器
  private val writeHandler: (obj: T) -> Unit,
) : StorageHandler<T> {
  /**
   * 向缓冲中添加一个写入对象
   */
  override fun add(obj: T) {
    writeHandler(obj)
  }
}