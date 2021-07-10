package cn.tursom.web

import cn.tursom.core.buffer.ByteBuffer

interface WebSocketHandler<in T : WebSocketContent> {
  fun connected(context: T)

  fun recvText(str: String, context: T)
  fun recvText(byteBuffer: ByteBuffer, context: T) = recvText(byteBuffer.getString(), context)

  fun recvBinary(bytes: ByteArray, context: T)
  fun recvBinary(byteBuffer: ByteBuffer, context: T) = recvBinary(byteBuffer.getBytes(), context)
}