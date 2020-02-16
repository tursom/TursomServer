package cn.tursom.core

import java.text.SimpleDateFormat

class ThreadLocalSimpleDateFormat(val format: String) : SimpThreadLocal<SimpleDateFormat>({
  SimpleDateFormat(format)
})