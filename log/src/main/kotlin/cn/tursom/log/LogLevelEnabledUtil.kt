package cn.tursom.log

import org.slf4j.Logger

val Logger?.traceEnabled
  get() = this?.isTraceEnabled ?: false
val Logger?.debugEnabled
  get() = this?.isDebugEnabled ?: false
val Logger?.infoEnabled
  get() = this?.isInfoEnabled ?: false
val Logger?.warnEnabled
  get() = this?.isWarnEnabled ?: false
val Logger?.errorEnabled
  get() = this?.isErrorEnabled ?: false