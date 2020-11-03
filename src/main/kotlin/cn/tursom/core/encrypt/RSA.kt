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
  modeOfOperation: BlockCipherModeOfOperation = BlockCipherModeOfOperation.ECB,
) : AbstractPublicKeyEncrypt("RSA", publicKey, privateKey, modeOfOperation = modeOfOperation) {

  val keySize get() = (publicKey as RSAPublicKey).modulus.bitLength()
  override val decryptMaxLen get() = keySize / 8
  override val encryptMaxLen get() = decryptMaxLen - 11

  override val public by lazy {
    if (privateKey == null) {
      this
    } else {
      RSA(publicKey, modeOfOperation = modeOfOperation)
    }
  }

  constructor(
    keyPair: KeyPair,
    modeOfOperation: BlockCipherModeOfOperation = BlockCipherModeOfOperation.ECB,
  ) : this(
    keyPair.public as RSAPublicKey,
    keyPair.private as RSAPrivateKey,
    modeOfOperation
  )

  constructor(
    keySize: Int = 1024,
    modeOfOperation: BlockCipherModeOfOperation = BlockCipherModeOfOperation.ECB,
  ) : this(
    KeyPairGenerator.getInstance("RSA").let {
      it.initialize(keySize)
      it.generateKeyPair()
    },
    modeOfOperation
  )

  constructor(
    publicKey: ByteArray,
    modeOfOperation: BlockCipherModeOfOperation = BlockCipherModeOfOperation.ECB,
  ) : this(
    KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicKey)) as RSAPublicKey,
    null,
    modeOfOperation
  )
}

