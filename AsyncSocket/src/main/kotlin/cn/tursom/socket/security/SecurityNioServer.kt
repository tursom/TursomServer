package cn.tursom.socket.security

import cn.tursom.core.encrypt.RSA
import cn.tursom.socket.AsyncSocket
import cn.tursom.socket.server.NioServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

class SecurityNioServer(
  port: Int,
  backlog: Int = 50,
  coroutineScope: CoroutineScope = GlobalScope,
  @Suppress("MemberVisibilityCanBePrivate") val rsa: RSA = RSA(),
  val handler: suspend AsyncSocket.() -> Unit
) : NioServer(port, backlog, coroutineScope, {
  AsyncSocketSecurityUtil.initActiveAESSocket(this, rsa)
  handler()
}) {
  constructor(
    port: Int,
    backlog: Int = 50,
    coroutineScope: CoroutineScope = GlobalScope,
    keySize: Int,
    handler: suspend AsyncSocket.() -> Unit
  ) : this(port, backlog, coroutineScope, RSA(keySize), handler)
}