package cn.tursom.core.reference

import java.io.Closeable

/**
 * 通过垃圾回收机制实现的自动关闭
 * 记得保存StrongReference对象防止Closeable意外关闭
 */
class AutoCloseFreeReference(
  private val closeable: Closeable,
  r: StrongReference<Closeable>,
) : FreeReference<StrongReference<Closeable>>(r) {
  override fun release() {
    closeable.close()
  }
}

fun <T : Closeable> T.registerAutoClose(): StrongReference<T> {
  val r = StrongReference(this)
  AutoCloseFreeReference(this, r)
  return r
}
