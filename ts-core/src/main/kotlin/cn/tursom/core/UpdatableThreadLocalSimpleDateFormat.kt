package cn.tursom.core

import java.text.SimpleDateFormat
import java.util.*

class UpdatableThreadLocalSimpleDateFormat(
  format: String = "YYYY-MM-dd'T'HH:mm:ssZZ",
) : UpdatableThreadLocal<SimpleDateFormat>({
  SimpleDateFormat(format)
}) {
  @Volatile
  var format: String = format
    set(value) {
      update { SimpleDateFormat(value) }
      field = value
    }

  fun format(date: Any) = get().format(date)
  fun format(date: Date) = get().format(date)
  fun parse(date: String) = get().parse(date)

  companion object {
    val iso8601 = UpdatableThreadLocalSimpleDateFormat("YYYY-MM-dd'T'HH:mm:ssZZ")
    val standard = UpdatableThreadLocalSimpleDateFormat("YYYY-MM-dd HH:mm:ss")
    val simp = UpdatableThreadLocalSimpleDateFormat("YY-MM-dd HH:mm:ss")
    val cn = UpdatableThreadLocalSimpleDateFormat("YYYY'年'MM'月'dd'日' HH'时'mm'分'ss'秒'")
  }
}