package cn.tursom.log.impl

import cn.tursom.log.TrySlf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class TrySlf4jImpl(name: String? = null) : TrySlf4j {
  constructor(clazz: Class<*>?) : this(clazz?.name)
  constructor(clazz: KClass<*>?) : this(clazz?.jvmName?.let {
    if (clazz.isCompanion) it.dropLast(10) else it
  })

  override val log: Logger? = try {
    LoggerFactory.getLogger(name ?: if (this.javaClass != TrySlf4jImpl::class.java) {
      val clazz = this.javaClass.kotlin
      clazz.jvmName.let {
        if (clazz.isCompanion) it.dropLast(10) else it
      }
    } else {
      throw NotImplementedError("")
    })
  } catch (e: Throwable) {
    null
  }
}