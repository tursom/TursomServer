package cn.tursom.forward.server

import cn.tursom.core.encrypt.AES
import cn.tursom.core.encrypt.AESPool
import cn.tursom.core.encrypt.PublicKeyEncrypt
import cn.tursom.core.encrypt.RSA
import cn.tursom.core.pool.Pool
import cn.tursom.forward.Forward
import cn.tursom.forward.impl.PassiveAesNettyForward
import cn.tursom.forward.socket.SocketServer

open class PassiveAesSocketForwardServer(
  port: Int,
  readTimeout: Int? = 60,
  writeTimeout: Int? = 60,
  private val aes: Pool<AES> = AESPool(128),
  private val publicKeyEncryptBuilder: (ByteArray) -> PublicKeyEncrypt = { RSA(it) },
  forward: () -> Forward
) : SocketServer(
  port, { PassiveAesNettyForward(it, forward(), aes.forceGet(), publicKeyEncryptBuilder) }, readTimeout, writeTimeout
)