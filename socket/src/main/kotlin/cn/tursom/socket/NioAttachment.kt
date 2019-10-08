package cn.tursom.socket

/**
 * 为了解决一个attachment不能携带多个数据的问题
 */
data class NioAttachment(var attachment: Any?, var protocol: INioProtocol)