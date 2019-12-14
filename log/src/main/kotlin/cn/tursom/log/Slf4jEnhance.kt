package cn.tursom.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.slf4jLogger(): Logger = LoggerFactory.getLogger(T::class.java)
