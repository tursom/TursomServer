package cn.tursom.core.ws

import kotlin.coroutines.CoroutineContext

class WebSocketClientContainer<C : WebSocketClient<C, H>, H : WebSocketHandler<C, H>>(
  val client: C,
) : CoroutineContext.Element {
  companion object Key : CoroutineContext.Key<WebSocketClientContainer<*, *>>

  override val key = Key
}
