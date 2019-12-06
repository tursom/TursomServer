package cn.tursom.web.ws

import cn.tursom.web.ExceptionContent
import cn.tursom.web.HttpHandler

interface WebSocketHandler<T : ExceptionContent> : HttpHandler<WsHttpContent, T> {
  override fun handle(content: WsHttpContent) {
    val frame = content.frame
    when (frame) {
      is TextWebSocketFrame -> {
        handle(content, frame)
      }
      else -> {
      }
    }
    handle(content, frame)
  }

  fun handle(content: WsHttpContent, frame: TextWebSocketFrame) {
  }

  fun handle(content: WsHttpContent, frame: WebSocketFrame) {
  }
}