package cn.tursom.web.ws

interface WebSocketContent {
  val frame: WebSocketFrame
  fun write(frame: WebSocketFrame)
}