package cn.tursom.core.encrypt

import java.security.PrivateKey
import java.security.PublicKey

interface PublicKeyEncrypt : Encrypt {
  val publicKey: PublicKey?
  val privateKey: PrivateKey?

  fun sign(data: ByteArray, digest: String = "SHA256"): ByteArray
  fun verify(data: ByteArray, sign: ByteArray, digest: String = "SHA256"): Boolean
}