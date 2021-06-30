package cn.tursom.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import ch.qos.logback.core.LayoutBase
import cn.tursom.core.ThreadLocalSimpleDateFormat
import cn.tursom.core.Utils.gson

open class JsonLayout : LayoutBase<ILoggingEvent>() {
  data class LayoutData(
    val logger: String,
    val date: String,
    val time: Long,
    val thread: String,
    val level: String,
    val message: String,
    val stackTrace: String,
  )

  override fun doLayout(event: ILoggingEvent): String = buildString {
    gson.toJson(getLogObject(event), this)
    append('\n')
  }

  protected open fun getLogObject(event: ILoggingEvent): Any = LayoutData(
    event.loggerName,
    ThreadLocalSimpleDateFormat.standard.format(event.timeStamp),
    event.timeStamp,
    event.threadName,
    event.level.levelStr,
    event.formattedMessage,
    ThrowableProxyUtil.asString(event.throwableProxy)
  )
}