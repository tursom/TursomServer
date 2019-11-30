package cn.tursom.log

import org.slf4j.Logger

interface Slf4j {
  val log: Logger
  val logger: Logger get() = log
  val sfl4j: Logger get() = log
}