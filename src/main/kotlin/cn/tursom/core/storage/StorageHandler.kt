package cn.tursom.core.storage

interface StorageHandler<T> : (T) -> Unit {
  /**
   * 向存储服务中写入一个对象
   */
  fun add(obj: T)

  override fun invoke(obj: T) = add(obj)
}