package cn.tursom.log

import org.slf4j.Logger
import org.slf4j.event.Level

inline fun lazyLog(crossinline toString: () -> String) = object {
  override fun toString(): String = toString()
}

@Suppress("NOTHING_TO_INLINE")
inline fun lazyPrettyMap(map: Iterator<Map.Entry<CharSequence, Any>>) = lazyLog {
  val sb = StringBuilder("{\n")
  map.forEach { (k, v) ->
    sb.append("  $k: $v\n")
  }
  sb.append("}")
  sb.toString()
}

@Suppress("NOTHING_TO_INLINE")
inline fun lazyPrettyMap(map: Iterable<Map.Entry<CharSequence, Any>>) = if (map.iterator().hasNext()) {
  lazyPrettyMap(map.iterator())
} else {
  "{}"
}

@Suppress("NOTHING_TO_INLINE")
inline fun lazyPrettyMap(map: Map<out CharSequence, Any>) = if (map.isNotEmpty()) {
  lazyPrettyMap(map.iterator())
} else {
  "{}"
}

fun Logger.isEnabled(level: Level) = when (level) {
  Level.ERROR -> isErrorEnabled
  Level.WARN -> isWarnEnabled
  Level.INFO -> isInfoEnabled
  Level.DEBUG -> isDebugEnabled
  Level.TRACE -> isTraceEnabled
}

fun Logger.log(level: Level, msg: String, args: Array<*>) = when (level) {
  Level.ERROR -> error(msg, *args)
  Level.WARN -> warn(msg, *args)
  Level.INFO -> info(msg, *args)
  Level.DEBUG -> debug(msg, *args)
  Level.TRACE -> trace(msg, *args)
}

@JvmName("friendlyLog")
fun Logger.log(level: Level, msg: String, vararg args: Any?) = when (level) {
  Level.ERROR -> error(msg, *args)
  Level.WARN -> warn(msg, *args)
  Level.INFO -> info(msg, *args)
  Level.DEBUG -> debug(msg, *args)
  Level.TRACE -> trace(msg, *args)
}

fun Logger.logCaller() = logCaller(Level.DEBUG)

fun Logger.logCaller(level: Level) {
  if (isEnabled(level)) {
    val e = Throwable()
    val stackTraceElement = e.stackTrace[1]
    log(
      level,
      "calling {}.{}({}:{})",
      stackTraceElement.className,
      stackTraceElement.methodName,
      stackTraceElement.fileName,
      stackTraceElement.lineNumber
    )
  }
}