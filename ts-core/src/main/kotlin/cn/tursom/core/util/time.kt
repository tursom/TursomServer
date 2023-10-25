@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.tursom.core.util

import java.util.concurrent.TimeUnit

fun Number.nanoSeconds() = NanoSeconds(toLong())
fun Number.microSeconds() = MicroSeconds(toLong())
fun Number.milliSeconds() = MilliSeconds(toLong())
fun Number.seconds() = Seconds(toLong())
fun Number.minutes() = Minutes(toLong())
fun Number.hours() = Hours(toLong())
fun Number.days() = Days(toLong())

fun Char.nanoSeconds() = NanoSeconds(code.toLong())
fun Char.microSeconds() = MicroSeconds(code.toLong())
fun Char.milliSeconds() = MilliSeconds(code.toLong())
fun Char.seconds() = Seconds(code.toLong())
fun Char.minutes() = Minutes(code.toLong())
fun Char.hours() = Hours(code.toLong())
fun Char.days() = Days(code.toLong())

fun Byte.nanoSeconds() = NanoSeconds(toLong())
fun Byte.microSeconds() = MicroSeconds(toLong())
fun Byte.milliSeconds() = MilliSeconds(toLong())
fun Byte.seconds() = Seconds(toLong())
fun Byte.minutes() = Minutes(toLong())
fun Byte.hours() = Hours(toLong())
fun Byte.days() = Days(toLong())

fun Short.nanoSeconds() = NanoSeconds(toLong())
fun Short.microSeconds() = MicroSeconds(toLong())
fun Short.milliSeconds() = MilliSeconds(toLong())
fun Short.seconds() = Seconds(toLong())
fun Short.minutes() = Minutes(toLong())
fun Short.hours() = Hours(toLong())
fun Short.days() = Days(toLong())

fun Int.nanoSeconds() = NanoSeconds(toLong())
fun Int.microSeconds() = MicroSeconds(toLong())
fun Int.milliSeconds() = MilliSeconds(toLong())
fun Int.seconds() = Seconds(toLong())
fun Int.minutes() = Minutes(toLong())
fun Int.hours() = Hours(toLong())
fun Int.days() = Days(toLong())

fun Long.nanoSeconds() = NanoSeconds(this)
fun Long.microSeconds() = MicroSeconds(this)
fun Long.milliSeconds() = MilliSeconds(this)
fun Long.seconds() = Seconds(this)
fun Long.minutes() = Minutes(this)
fun Long.hours() = Hours(this)
fun Long.days() = Days(this)

fun Float.nanoSeconds() = NanoSeconds(toLong())
fun Float.microSeconds() = MicroSeconds(toLong())
fun Float.milliSeconds() = MilliSeconds(toLong())
fun Float.seconds() = Seconds(toLong())
fun Float.minutes() = Minutes(toLong())
fun Float.hours() = Hours(toLong())
fun Float.days() = Days(toLong())

fun Double.nanoSeconds() = NanoSeconds(toLong())
fun Double.microSeconds() = MicroSeconds(toLong())
fun Double.milliSeconds() = MilliSeconds(toLong())
fun Double.seconds() = Seconds(toLong())
fun Double.minutes() = Minutes(toLong())
fun Double.hours() = Hours(toLong())
fun Double.days() = Days(toLong())

sealed interface Time {
  val duration: Long
  val timeUnit: TimeUnit
  fun convert(timeUnit: TimeUnit): Long = this.timeUnit.convert(duration, timeUnit)
  fun toNanos(): Long = timeUnit.toNanos(duration)
  fun toMicros(): Long = timeUnit.toMicros(duration)
  fun toMillis(): Long = timeUnit.toMillis(duration)
  fun toSeconds(): Long = timeUnit.toSeconds(duration)
  fun toMinutes(): Long = timeUnit.toMinutes(duration)
  fun toHours(): Long = timeUnit.toHours(duration)
  fun toDays(): Long = timeUnit.toDays(duration)
  fun timedWait(obj: Any) = timeUnit.timedWait(obj, duration)
  fun timedJoin(thread: Thread) = timeUnit.timedJoin(thread, duration)
  fun sleep() = timeUnit.sleep(duration)
  suspend fun delay() {
    kotlinx.coroutines.delay(timeUnit.toMillis(duration))
  }
}

@JvmInline
value class NanoSeconds(override val duration: Long) : Time {
  override val timeUnit: TimeUnit get() = TimeUnit.NANOSECONDS
  override fun convert(timeUnit: TimeUnit): Long = TimeUnit.NANOSECONDS.convert(duration, timeUnit)
  override fun toNanos(): Long = TimeUnit.NANOSECONDS.toNanos(duration)
  override fun toMicros(): Long = TimeUnit.NANOSECONDS.toMicros(duration)
  override fun toMillis(): Long = TimeUnit.NANOSECONDS.toMillis(duration)
  override fun toSeconds(): Long = TimeUnit.NANOSECONDS.toSeconds(duration)
  override fun toMinutes(): Long = TimeUnit.NANOSECONDS.toMinutes(duration)
  override fun toHours(): Long = TimeUnit.NANOSECONDS.toHours(duration)
  override fun toDays(): Long = TimeUnit.NANOSECONDS.toDays(duration)
  override fun timedWait(obj: Any) = TimeUnit.NANOSECONDS.timedWait(obj, duration)
  override fun timedJoin(thread: Thread) = TimeUnit.NANOSECONDS.timedJoin(thread, duration)
  override fun sleep() = TimeUnit.NANOSECONDS.sleep(duration)
  override suspend fun delay() {
    kotlinx.coroutines.delay(TimeUnit.NANOSECONDS.toMillis(duration))
  }
}

@JvmInline
value class MicroSeconds(override val duration: Long) : Time {
  override val timeUnit: TimeUnit get() = TimeUnit.MICROSECONDS
  override fun convert(timeUnit: TimeUnit): Long = TimeUnit.MICROSECONDS.convert(duration, timeUnit)
  override fun toNanos(): Long = TimeUnit.MICROSECONDS.toNanos(duration)
  override fun toMicros(): Long = TimeUnit.MICROSECONDS.toMicros(duration)
  override fun toMillis(): Long = TimeUnit.MICROSECONDS.toMillis(duration)
  override fun toSeconds(): Long = TimeUnit.MICROSECONDS.toSeconds(duration)
  override fun toMinutes(): Long = TimeUnit.MICROSECONDS.toMinutes(duration)
  override fun toHours(): Long = TimeUnit.MICROSECONDS.toHours(duration)
  override fun toDays(): Long = TimeUnit.MICROSECONDS.toDays(duration)
  override fun timedWait(obj: Any) = TimeUnit.MICROSECONDS.timedWait(obj, duration)
  override fun timedJoin(thread: Thread) = TimeUnit.MICROSECONDS.timedJoin(thread, duration)
  override fun sleep() = TimeUnit.MICROSECONDS.sleep(duration)
  override suspend fun delay() {
    kotlinx.coroutines.delay(TimeUnit.MICROSECONDS.toMillis(duration))
  }
}

@JvmInline
value class MilliSeconds(override val duration: Long) : Time {
  override val timeUnit: TimeUnit get() = TimeUnit.MILLISECONDS
  override fun convert(timeUnit: TimeUnit): Long = TimeUnit.MILLISECONDS.convert(duration, timeUnit)
  override fun toNanos(): Long = TimeUnit.MILLISECONDS.toNanos(duration)
  override fun toMicros(): Long = TimeUnit.MILLISECONDS.toMicros(duration)
  override fun toMillis(): Long = TimeUnit.MILLISECONDS.toMillis(duration)
  override fun toSeconds(): Long = TimeUnit.MILLISECONDS.toSeconds(duration)
  override fun toMinutes(): Long = TimeUnit.MILLISECONDS.toMinutes(duration)
  override fun toHours(): Long = TimeUnit.MILLISECONDS.toHours(duration)
  override fun toDays(): Long = TimeUnit.MILLISECONDS.toDays(duration)
  override fun timedWait(obj: Any) = TimeUnit.MILLISECONDS.timedWait(obj, duration)
  override fun timedJoin(thread: Thread) = TimeUnit.MILLISECONDS.timedJoin(thread, duration)
  override fun sleep() = TimeUnit.MILLISECONDS.sleep(duration)
  override suspend fun delay() {
    kotlinx.coroutines.delay(TimeUnit.MILLISECONDS.toMillis(duration))
  }
}

@JvmInline
value class Seconds(override val duration: Long) : Time {
  override val timeUnit: TimeUnit get() = TimeUnit.SECONDS
  override fun convert(timeUnit: TimeUnit): Long = TimeUnit.SECONDS.convert(duration, timeUnit)
  override fun toNanos(): Long = TimeUnit.SECONDS.toNanos(duration)
  override fun toMicros(): Long = TimeUnit.SECONDS.toMicros(duration)
  override fun toMillis(): Long = TimeUnit.SECONDS.toMillis(duration)
  override fun toSeconds(): Long = TimeUnit.SECONDS.toSeconds(duration)
  override fun toMinutes(): Long = TimeUnit.SECONDS.toMinutes(duration)
  override fun toHours(): Long = TimeUnit.SECONDS.toHours(duration)
  override fun toDays(): Long = TimeUnit.SECONDS.toDays(duration)
  override fun timedWait(obj: Any) = TimeUnit.SECONDS.timedWait(obj, duration)
  override fun timedJoin(thread: Thread) = TimeUnit.SECONDS.timedJoin(thread, duration)
  override fun sleep() = TimeUnit.SECONDS.sleep(duration)
  override suspend fun delay() {
    kotlinx.coroutines.delay(TimeUnit.SECONDS.toMillis(duration))
  }
}

@JvmInline
value class Minutes(override val duration: Long) : Time {
  override val timeUnit: TimeUnit get() = TimeUnit.MINUTES
  override fun convert(timeUnit: TimeUnit): Long = TimeUnit.MINUTES.convert(duration, timeUnit)
  override fun toNanos(): Long = TimeUnit.MINUTES.toNanos(duration)
  override fun toMicros(): Long = TimeUnit.MINUTES.toMicros(duration)
  override fun toMillis(): Long = TimeUnit.MINUTES.toMillis(duration)
  override fun toSeconds(): Long = TimeUnit.MINUTES.toSeconds(duration)
  override fun toMinutes(): Long = TimeUnit.MINUTES.toMinutes(duration)
  override fun toHours(): Long = TimeUnit.MINUTES.toHours(duration)
  override fun toDays(): Long = TimeUnit.MINUTES.toDays(duration)
  override fun timedWait(obj: Any) = TimeUnit.MINUTES.timedWait(obj, duration)
  override fun timedJoin(thread: Thread) = TimeUnit.MINUTES.timedJoin(thread, duration)
  override fun sleep() = TimeUnit.MINUTES.sleep(duration)
  override suspend fun delay() {
    kotlinx.coroutines.delay(TimeUnit.MINUTES.toMillis(duration))
  }
}

@JvmInline
value class Hours(override val duration: Long) : Time {
  override val timeUnit: TimeUnit get() = TimeUnit.HOURS
  override fun convert(timeUnit: TimeUnit): Long = TimeUnit.HOURS.convert(duration, timeUnit)
  override fun toNanos(): Long = TimeUnit.HOURS.toNanos(duration)
  override fun toMicros(): Long = TimeUnit.HOURS.toMicros(duration)
  override fun toMillis(): Long = TimeUnit.HOURS.toMillis(duration)
  override fun toSeconds(): Long = TimeUnit.HOURS.toSeconds(duration)
  override fun toMinutes(): Long = TimeUnit.HOURS.toMinutes(duration)
  override fun toHours(): Long = TimeUnit.HOURS.toHours(duration)
  override fun toDays(): Long = TimeUnit.HOURS.toDays(duration)
  override fun timedWait(obj: Any) = TimeUnit.HOURS.timedWait(obj, duration)
  override fun timedJoin(thread: Thread) = TimeUnit.HOURS.timedJoin(thread, duration)
  override fun sleep() = TimeUnit.HOURS.sleep(duration)
  override suspend fun delay() {
    kotlinx.coroutines.delay(TimeUnit.HOURS.toMillis(duration))
  }
}

@JvmInline
value class Days(override val duration: Long) : Time {
  override val timeUnit: TimeUnit get() = TimeUnit.DAYS
  override fun convert(timeUnit: TimeUnit): Long = TimeUnit.DAYS.convert(duration, timeUnit)
  override fun toNanos(): Long = TimeUnit.DAYS.toNanos(duration)
  override fun toMicros(): Long = TimeUnit.DAYS.toMicros(duration)
  override fun toMillis(): Long = TimeUnit.DAYS.toMillis(duration)
  override fun toSeconds(): Long = TimeUnit.DAYS.toSeconds(duration)
  override fun toMinutes(): Long = TimeUnit.DAYS.toMinutes(duration)
  override fun toHours(): Long = TimeUnit.DAYS.toHours(duration)
  override fun toDays(): Long = TimeUnit.DAYS.toDays(duration)
  override fun timedWait(obj: Any) = TimeUnit.DAYS.timedWait(obj, duration)
  override fun timedJoin(thread: Thread) = TimeUnit.DAYS.timedJoin(thread, duration)
  override fun sleep() = TimeUnit.DAYS.sleep(duration)
  override suspend fun delay() {
    kotlinx.coroutines.delay(TimeUnit.DAYS.toMillis(duration))
  }
}
