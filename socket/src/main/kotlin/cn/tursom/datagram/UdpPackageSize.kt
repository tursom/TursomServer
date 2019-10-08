package cn.tursom.datagram

object UdpPackageSize {
	//定义不同环境下数据报的最大大小
	const val LANNetLen = 1472
	const val internetLen = 548
	const val defaultLen = internetLen
}