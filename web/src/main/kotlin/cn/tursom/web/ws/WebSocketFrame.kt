package cn.tursom.web.ws

import cn.tursom.core.buffer.ByteBuffer

interface WebSocketFrame {
  val data: ByteBuffer
}