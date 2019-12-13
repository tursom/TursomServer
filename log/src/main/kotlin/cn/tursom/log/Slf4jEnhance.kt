package cn.tursom.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> LoggerFactory.getLogger(): Logger = LoggerFactory.getLogger(T::class.java)