package cn.tursom.core.timer

interface Timer {
	fun exec(timeout: Long, task: () -> Unit): TimerTask
}
