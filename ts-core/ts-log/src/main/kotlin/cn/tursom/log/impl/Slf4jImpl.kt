package cn.tursom.log.impl

import cn.tursom.core.util.getCallerClassName
import cn.tursom.log.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

open class Slf4jImpl constructor(
  @field:Transient
  override val log: Logger,
) : Slf4j, Logger by log {
  constructor(name: String? = null) : this(LoggerFactory.getLogger(name ?: loggerName))
  constructor(target: Class<*>?) : this(target?.canonicalName)
  constructor(target: Any?) : this(target?.let { it::class })
  constructor(target: KClass<*>?) : this(target?.let { clazz ->
    clazz.jvmName.let {
      if (clazz.isCompanion) {
        it.dropLast(10)
      } else {
        it
      }
    }
  })

  @Suppress("MemberVisibilityCanBePrivate", "NOTHING_TO_INLINE")
  companion object {
    private val thisClassName = listOf(
      this::class.java.name.dropLast(10),
      this::class.java.name,
    )
    private val loggerName: String
      get() = getCallerClassName(thisClassName)?.substringBefore('$')
        ?: throw UnsupportedOperationException()

    inline fun getLogger(name: String): Logger = LoggerFactory.getLogger(name)

    fun getLogger(): Logger = LoggerFactory.getLogger(loggerName)

    inline fun getLogger(target: Any): Logger = getLogger(if (target::class != Slf4jImpl::class) {
      val kClass = target::class
      kClass.jvmName.let {
        if (kClass.isCompanion) {
          it.dropLast(10)
        } else {
          it
        }
      }
    } else {
      throw NotImplementedError("there is no default logger name")
    })
  }
}