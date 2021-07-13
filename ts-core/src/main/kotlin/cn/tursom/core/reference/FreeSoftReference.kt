package cn.tursom.core.reference

import java.lang.ref.SoftReference


abstract class FreeSoftReference<T>(
  referent: T,
) : SoftReference<T>(referent, ReleasableReference.referenceQueue),
  ReleasableReference {

  private var cancel: Boolean = false

  init {
    @Suppress("LeakingThis")
    ReleasableReference.hosting(this)
  }

  override fun enqueue(): Boolean {
    return if (cancel) {
      false
    } else {
      super.enqueue()
    }
  }

  override fun cancel() {
    super.cancel()
    cancel = true
  }
}
