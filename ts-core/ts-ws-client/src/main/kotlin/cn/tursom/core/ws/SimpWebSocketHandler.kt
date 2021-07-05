package cn.tursom.core.ws

/**
 * using like
 *   val handler = object : AbstractWebSocketHandler<SimpWebSocketClient<SimpWebSocketHandler>, SimpWebSocketHandler>(),
 *     SimpWebSocketHandler {
 *   }
 *   SimpWebSocketClient("", handler)
 * to impl AbstractWebSocketHandler's function
 */
interface SimpWebSocketHandler : WebSocketHandler<SimpWebSocketClient<SimpWebSocketHandler>, SimpWebSocketHandler>
