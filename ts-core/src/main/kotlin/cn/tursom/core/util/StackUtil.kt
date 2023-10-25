@file:Suppress("unused")

package cn.tursom.core.util

private object StackUtil {
  val stackWalker: StackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
}

fun getCallerClass(thisClassName: List<String>): Class<*>? {
  var clazz: Class<*>?
  var callStackDepth = 1
  do {
    clazz = getCallerClass(callStackDepth++)
    val clazzName = clazz?.name
    if (clazzName != "cn.tursom.core.UtilsKt" && clazzName !in thisClassName) {
      break
    }
  } while (clazz != null)
  return clazz
}

fun getCallerClassName(thisClassName: List<String>): String? {
  return getCallerClass(thisClassName)?.name
}

fun getCallerClass(callStackDepth: Int): Class<*>? {
  return StackUtil.stackWalker.walk { frameStream ->
    frameStream.skip((callStackDepth).toLong()).findFirst().orElse(null)?.declaringClass
  }
}

fun getCallerClassName(callStackDepth: Int): String? {
  return getCallerClass(callStackDepth)?.name
}

