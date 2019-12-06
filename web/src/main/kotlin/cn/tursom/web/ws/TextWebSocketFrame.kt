package cn.tursom.web.ws

interface TextWebSocketFrame : WebSocketFrame {
  val text: String
}