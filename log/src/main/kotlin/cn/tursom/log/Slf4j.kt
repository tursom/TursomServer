package cn.tursom.log

import org.slf4j.Logger

interface Slf4j : TrySlf4j {
  override val log: Logger
  override val logger get() = log
  override val sfl4j get() = log
}