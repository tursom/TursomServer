package cn.tursom.core.encrypt

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.DSAPrivateKey
import java.security.interfaces.DSAPublicKey
import java.security.spec.X509EncodedKeySpec

@Suppress("unused", "MemberVisibilityCanBePrivate")
class DSA(
  publicKey: DSAPublicKey,
  privateKey: DSAPrivateKey? = null,
  algorithm: String = "DSA",
) : AbstractPublicKeyEncrypt(algorithm, publicKey, privateKey) {
  override val decryptMaxLen = Int.MAX_VALUE
  override val encryptMaxLen = Int.MAX_VALUE

  override val public by lazy {
    if (privateKey == null) {
      this
    } else {
      DSA(publicKey)
    }
  }

  constructor(keyPair: KeyPair, algorithm: String = "DSA") : this(
    keyPair.public as DSAPublicKey,
    keyPair.private as DSAPrivateKey,
    algorithm,
  )

  constructor(keySize: Int = 1024, algorithm: String = "DSA") : this(
    KeyPairGenerator.getInstance("DSA").let {
      it.initialize(keySize)
      it.generateKeyPair()
    },
    algorithm,
  )

  constructor(publicKey: ByteArray, algorithm: String = "DSA") : this(
    KeyFactory.getInstance("DSA").generatePublic(X509EncodedKeySpec(publicKey)) as DSAPublicKey,
    null,
    algorithm,
  )
}