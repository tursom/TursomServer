package cn.tursom.core.encrypt

import cn.tursom.core.Utils
import cn.tursom.core.datastruct.concurrent.BlockingArrayList
import cn.tursom.core.pool.Pool

open class EncryptPool<T : Encrypt>(
  initSize: Int = 0,
  encryptBuilder: () -> T,
) : Pool<T> {
  private val aesPool = BlockingArrayList<T>()

  init {
    repeat(initSize) {
      put(encryptBuilder())
    }
  }

  override fun put(cache: T): Boolean {
    return aesPool.add(cache)
  }

  override fun get(): T = aesPool[Utils.random.nextInt(0, aesPool.size - 1)]
}