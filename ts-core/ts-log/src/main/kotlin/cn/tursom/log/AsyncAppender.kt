package cn.tursom.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.AsyncAppenderBase
import ch.qos.logback.core.spi.AppenderAttachableImpl
import cn.tursom.core.delegation.ReflectionDelegatedField.Companion.superField

class AsyncAppender : AsyncAppenderBase<ILoggingEvent>() {
    private var includeCallerData = false
    private val aai: AppenderAttachableImpl<ILoggingEvent> by superField("aai")
    private var appenderCount: Int by superField("appenderCount")

    /**
     * Events of level TRACE, DEBUG and INFO are deemed to be discardable.
     * @param event
     * @return true if the event is of level TRACE, DEBUG or INFO false otherwise.
     */
    override fun isDiscardable(event: ILoggingEvent): Boolean {
        val level = event.level
        return level.toInt() <= Level.INFO_INT
    }

    override fun preprocess(eventObject: ILoggingEvent) {
        eventObject.prepareForDeferredProcessing()
        if (includeCallerData) eventObject.callerData
    }

    override fun addAppender(newAppender: Appender<ILoggingEvent>) {
        addInfo("Attaching appender named [${newAppender.name}] to AsyncAppender.")
        aai.addAppender(newAppender)
        appenderCount++
    }
}

