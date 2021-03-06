package cn.tursom.core

import kotlin.concurrent.thread


object CurrentTimeMillisClock {
  @Volatile
  private var tick: Long = System.currentTimeMillis()

  val now get() = tick

  init {
    thread(name = "current-time-millis", isDaemon = true) {
      while (true) {
        tick = System.currentTimeMillis()
        Thread.sleep(1)
      }
    }
  }

  //val now get() = System.currentTimeMillis()
}