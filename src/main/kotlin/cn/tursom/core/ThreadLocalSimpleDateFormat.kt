package cn.tursom.core

import java.text.SimpleDateFormat

class ThreadLocalSimpleDateFormat(val format: String) {
  private val threadLocal = ThreadLocal<SimpleDateFormat>()

  fun get(): SimpleDateFormat {
    var simpleDateFormat: SimpleDateFormat? = threadLocal.get()
    if (simpleDateFormat == null) {
      simpleDateFormat = SimpleDateFormat(format)
      threadLocal.set(simpleDateFormat)
    }
    return simpleDateFormat
  }
}