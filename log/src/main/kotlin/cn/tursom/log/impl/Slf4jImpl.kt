package cn.tursom.log.impl

import cn.tursom.log.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.jvm.jvmName

class Slf4jImpl(name: String? = null) : Slf4j {
  override val log: Logger = LoggerFactory.getLogger(name ?: if (this::class.java != Slf4jImpl::class.java) {
    val clazz = this::class
    clazz.jvmName.let {
      if (clazz.isCompanion) it.dropLast(10) else it
    }
  } else {
    throw NotImplementedError("")
  })
}