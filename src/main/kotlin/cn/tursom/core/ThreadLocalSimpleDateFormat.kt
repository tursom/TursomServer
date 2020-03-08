package cn.tursom.core

import java.text.SimpleDateFormat
import java.util.*

class ThreadLocalSimpleDateFormat(val format: String = "YYYY-MM-dd'T'HH:mm:ssZZ") : SimpThreadLocal<SimpleDateFormat>({
  SimpleDateFormat(format)
}) {
  fun format(date: Any) = get().format(date)
  fun format(date: Date) = get().format(date)
  fun parse(date: String) = get().parse(date)

  companion object {
    val iso8601 = ThreadLocalSimpleDateFormat()
    val standard = ThreadLocalSimpleDateFormat("YYYY-MM-dd HH:mm:ssZZ")
    val simp = ThreadLocalSimpleDateFormat("YY-MM-dd HH:mm:ss")
    val cn = ThreadLocalSimpleDateFormat("YYYY'年'MM'月'dd'日' HH'时'mm'分'ss'秒'")
  }
}