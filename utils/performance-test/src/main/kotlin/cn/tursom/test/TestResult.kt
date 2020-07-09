package cn.tursom.test

import java.util.concurrent.TimeUnit

data class TestResult(
  val startTime: Long,
  val endTime: Long,
  val line95: Long,
  val line98: Long,
  val line99: Long,
  val usingTime: Long = endTime - startTime,
  val usingTimeS: Long = TimeUnit.SECONDS.convert(usingTime, TimeUnit.MILLISECONDS),
  val t95: Long = line95 - startTime,
  val t98: Long = line98 - startTime,
  val t99: Long = line99 - startTime,
  val t95s: Long = TimeUnit.SECONDS.convert(t95, TimeUnit.MILLISECONDS),
  val t98s: Long = TimeUnit.SECONDS.convert(t98, TimeUnit.MILLISECONDS),
  val t99s: Long = TimeUnit.SECONDS.convert(t99, TimeUnit.MILLISECONDS)
) {
}