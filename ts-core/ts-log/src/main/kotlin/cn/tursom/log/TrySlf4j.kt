package cn.tursom.log

import org.slf4j.Logger

interface TrySlf4j {
  val log: Logger?
  val logger get() = log
  val sfl4j get() = log
}