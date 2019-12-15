package cn.tursom.log

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