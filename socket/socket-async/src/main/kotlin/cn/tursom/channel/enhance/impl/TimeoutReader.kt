package cn.tursom.channel.enhance.impl

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.timer.TimerTask
import cn.tursom.core.timer.WheelTimer
import cn.tursom.channel.enhance.SocketReader

class TimeoutReader<Read>(val prevReader: SocketReader<Read>, val timeout: Long = 5000L) : SocketReader<Read> {
	private var timerTask: TimerTask? = null
	override suspend fun get(buffer: ByteBuffer, timeout: Long): Read {
		timerTask?.cancel()
		timerTask = timer.exec(this.timeout) {
			prevReader.close()
		}
		return prevReader.get(buffer, timeout)
	}

	override fun close() {
		prevReader.close()
	}

	companion object {
		val timer = WheelTimer.timer
	}
}