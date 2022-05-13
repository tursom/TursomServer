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
  privateKey: RSAPrivateKey? = null,
  algorithm: String = "RSA",
) : AbstractPublicKeyEncrypt(algorithm, publicKey, privateKey) {
  val keySize get() = (publicKey as RSAPublicKey).modulus.bitLength()

  override val decryptMaxLen get() = keySize / 8
  override val encryptMaxLen get() = decryptMaxLen - 11

  override val public by lazy {
    if (privateKey == null) {
      this
    } else {
      RSA(publicKey)
    }
  }

  constructor(
    keyPair: KeyPair,
    algorithm: String = "RSA",
  ) : this(
    keyPair.public as RSAPublicKey,
    keyPair.private as RSAPrivateKey,
    algorithm,
  )

  constructor(
    keySize: Int = 1024,
    algorithm: String = "RSA",
  ) : this(
    KeyPairGenerator.getInstance("RSA").let {
      it.initialize(keySize)
      it.generateKeyPair()
    },
    algorithm,
  )

  constructor(
    publicKey: ByteArray,
    algorithm: String = "RSA",
  ) : this(
    KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicKey)) as RSAPublicKey,
    null,
    algorithm,
  )
}

