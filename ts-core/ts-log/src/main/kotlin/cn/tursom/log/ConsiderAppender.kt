package cn.tursom.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.Context
import ch.qos.logback.core.spi.FilterAttachable
import ch.qos.logback.core.spi.FilterAttachableImpl
import ch.qos.logback.core.status.Status

class ConsiderAppender : Appender<ILoggingEvent>,
  FilterAttachable<ILoggingEvent> by FilterAttachableImpl() {

  private val appenderList: MutableList<Appender<ILoggingEvent>> = ArrayList()
  var consider: String? = null
  private var activeValue: MutableList<String> = ArrayList()
  var matchIfNull: Boolean = false
  private val notContain: MutableList<String> = ArrayList()
  private var name: String? = null
  private var started: Boolean = true
  private var context: Context? = null

  fun addAppender(appender: Appender<ILoggingEvent>) {
    appenderList.add(appender)
  }

  fun addActiveValue(activeValue: String) {
    this.activeValue.add(activeValue)
  }

  fun addNotContain(notContain: String) {
    this.notContain.add(notContain)
  }

  override fun start() {
    started = true
    when {
      consider == null -> appenderList.clear()
      consider in activeValue -> Unit
      consider in notContain -> appenderList.clear()
      matchIfNull -> Unit
      else -> appenderList.clear()
    }
    appenderList.forEach(Appender<ILoggingEvent>::start)
  }

  override fun stop() {
    started = false
    appenderList.forEach(Appender<ILoggingEvent>::stop)
  }

  override fun isStarted(): Boolean = started

  override fun setContext(context: Context?) {
    this.context = context
    appenderList.forEach {
      it.context = context
    }
  }

  override fun getContext(): Context? = context

  override fun addStatus(status: Status?) {
    appenderList.forEach {
      it.addStatus(status)
    }
  }

  override fun addInfo(msg: String?) {
    appenderList.forEach {
      it.addInfo(msg)
    }
  }

  override fun addInfo(msg: String?, ex: Throwable?) {
    appenderList.forEach {
      it.addInfo(msg, ex)
    }
  }

  override fun addWarn(msg: String?) {
    appenderList.forEach {
      it.addWarn(msg)
    }
  }

  override fun addWarn(msg: String?, ex: Throwable?) {
    appenderList.forEach {
      it.addWarn(msg, ex)
    }
  }

  override fun addError(msg: String?) {
    appenderList.forEach {
      it.addError(msg)
    }
  }

  override fun addError(msg: String?, ex: Throwable?) {
    appenderList.forEach {
      it.addError(msg, ex)
    }
  }

  override fun getName(): String? = name

  override fun doAppend(event: ILoggingEvent?) {
    appenderList.forEach {
      it.doAppend(event)
    }
  }

  override fun setName(name: String?) {
    this.name = name
  }
}