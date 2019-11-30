package cn.tursom.log.impl

import cn.tursom.log.Slf4j
import cn.tursom.log.TrySlf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class Slf4jImpl(name: String? = null) : Slf4j, TrySlf4j {
  constructor(clazz: Class<*>?) : this(clazz?.name)
  constructor(clazz: KClass<*>?) : this(clazz?.jvmName?.let {
    if (clazz.isCompanion) it.dropLast(10) else it
  })

  override val log: Logger = LoggerFactory.getLogger(name ?: if (this.javaClass != Slf4jImpl::class.java) {
    val clazz = this.javaClass.kotlin
    clazz.jvmName.let {
      if (clazz.isCompanion) it.dropLast(10) else it
    }
  } else {
    throw NotImplementedError("")
  })

  override val logger: Logger get() = log
  override val sfl4j: Logger get() = log
}