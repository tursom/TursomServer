package cn.tursom.log

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.OutputStreamAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.util.FileSize
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

inline fun <reified T> T.slf4jLogger(): Logger = LoggerFactory.getLogger(T::class.java)

fun setLogLevel(level: Level, pkg: String = Logger.ROOT_LOGGER_NAME) {
  val root = LoggerFactory.getLogger(pkg) as ch.qos.logback.classic.Logger
  root.level = ch.qos.logback.classic.Level.toLevel(level.toString())
}

fun setLogLevel(level: String, pkg: String = Logger.ROOT_LOGGER_NAME) = setLogLevel(Level.valueOf(level.toUpperCase()), pkg)


/**
 * desc: 策略：每份日志文件最大1000KB，真实大小可能会略大，且最多保存7天
 * author: Xubin
 * date: 2017/4/17 16:06
 * update: 2017/4/17
 */
fun configureLogbackDirectly(logDir: String, filePrefix: String) {
  val context: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
  val rollingFileAppender = RollingFileAppender<ILoggingEvent>().also { rollingFileAppender ->
    rollingFileAppender.isAppend = true
    rollingFileAppender.context = context
    rollingFileAppender.encoder = PatternLayoutEncoder().also { encoder ->
      encoder.pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
      encoder.context = context
      encoder.start()
    }
  }
  val rollingPolicy = TimeBasedRollingPolicy<ILoggingEvent>()
  rollingPolicy.fileNamePattern = "$logDir/${filePrefix}_%d{yyyyMMdd}_%i.log"
  rollingPolicy.maxHistory = 7
  rollingPolicy.setParent(rollingFileAppender)
  rollingPolicy.context = context
  val sizeAndTimeBasedFNATP = SizeAndTimeBasedFNATP<ILoggingEvent>()
  sizeAndTimeBasedFNATP.setMaxFileSize(FileSize.valueOf("1000KB"))
  sizeAndTimeBasedFNATP.context = context
  sizeAndTimeBasedFNATP.setTimeBasedRollingPolicy(rollingPolicy)
  rollingPolicy.timeBasedFileNamingAndTriggeringPolicy = sizeAndTimeBasedFNATP
  rollingPolicy.start()
  sizeAndTimeBasedFNATP.start()
  rollingFileAppender.rollingPolicy = rollingPolicy
  rollingFileAppender.start()

  val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
  root.addAppender(rollingFileAppender)
}

val defaultColorfulPattern = LogbackPattern.make {
  +color { green }(+date)["yyyy-MM-dd HH:mm:ss.SSS"] + " " +
    +color { magenta }("[${+thread}]") + " " +
    +color { highlight }(+level.left(5)) + " " +
    +color { cyan }("[${+logger["20"].right(20, 20)}]") + " - " +
    +color { highlight }(+message) + " " +
    +nextLine
}

fun colorfulConsoleLogger(appender: OutputStreamAppender<*>, pattern: String = defaultColorfulPattern) {
  LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).warn("change console color: {}", pattern)
  val context: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
  appender.encoder = PatternLayoutEncoder().also { encoder ->
    encoder.pattern = pattern
    encoder.context = context
    encoder.start()
  }
}

fun colorfulConsoleLogger(pattern: String = defaultColorfulPattern) {
  (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger)
    .iteratorForAppenders()
    .forEach {
      if (it is ConsoleAppender) {
        colorfulConsoleLogger(it, pattern)
      }
    }
}