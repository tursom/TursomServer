package cn.tursom.core

import java.text.SimpleDateFormat

class ThreadLocalSimpleDateFormat(val format: String = "YYYY-MM-dd'T'HH:mm:ssZZ") : SimpThreadLocal<SimpleDateFormat>({
  SimpleDateFormat(format)
})