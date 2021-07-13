package cn.tursom.core.reference

import java.lang.ref.PhantomReference


abstract class FreeReference<T>(
  referent: T,
) : PhantomReference<T>(referent, ReleasableReference.referenceQueue),
  ReleasableReference {

  private var cancel: Boolean = false

  override fun enqueue(): Boolean {
    return if (cancel) {
      false
    } else {
      super.enqueue()
    }
  }

  fun cancel() {
    cancel = true
  }
}
