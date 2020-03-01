package cn.tursom.core.encrypt

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec

@Suppress("unused", "MemberVisibilityCanBePrivate")
class RSA(
  publicKey: RSAPublicKey,
  privateKey: RSAPrivateKey? = null
) : AbstractPublicKeyEncrypt("RSA", publicKey, privateKey) {

  val keySize = publicKey.modulus.bitLength()
  override val decryptMaxLen = keySize / 8
  override val encryptMaxLen = decryptMaxLen - 11

  override  val public by lazy {
    if (privateKey == null) {
      this
    } else {
      RSA(publicKey)
    }
  }

  constructor(keyPair: KeyPair) : this(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey)

  constructor(keySize: Int = 1024) : this(KeyPairGenerator.getInstance("RSA").let {
    it.initialize(keySize)
    it.generateKeyPair()
  })

  constructor(publicKey: ByteArray) : this(KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicKey)) as RSAPublicKey)
}

