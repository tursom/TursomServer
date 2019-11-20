package cn.tursom.core.timer

interface TaskQueue {
  fun offer(task: () -> Unit, timeout: Long): TimerTask
  fun offer(task: TimerTask): TimerTask
  fun take(): TimerTask?
}