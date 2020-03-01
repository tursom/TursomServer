package cn.tursom.core.encrypt

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


@Suppress("unused")
class RSA(val publicKey: RSAPublicKey, val privateKey: RSAPrivateKey? = null) : Encrypt {
  val publicKeyEncoded get() = publicKey.encoded!!
  val privateKeyEncoded get() = privateKey?.encoded

  private val encryptCipher = Cipher.getInstance("RSA")!!
  private val decryptCipher = Cipher.getInstance("RSA")!!

  init {
    encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
    if (privateKey != null) decryptCipher.init(Cipher.DECRYPT_MODE, privateKey)
  }

  constructor(keyPair: KeyPair) : this(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey)

  constructor(keySize: Int = 1024) : this(KeyPairGenerator.getInstance("RSA").let {
    it.initialize(keySize)
    it.generateKeyPair()
  })

  constructor(publicKey: ByteArray) : this(KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicKey)) as RSAPublicKey)

  override fun encrypt(data: ByteArray, offset: Int, size: Int): ByteArray {
    return if (size < 117)
      encryptCipher.doFinal(data, offset, size)
    else {
      val buffer = ByteArray(size / 117 * 128 + 128)
      var readPosition = offset
      var decodeIndex = 0

      while (readPosition + 117 < size) {
        decodeIndex += encryptCipher.doFinal(data, readPosition, 117, buffer, decodeIndex)
        readPosition += 117
      }
      decodeIndex += encryptCipher.doFinal(data, readPosition, size - readPosition, buffer, decodeIndex)

      buffer.copyOf(decodeIndex)
    }
  }

  override fun decrypt(data: ByteArray, offset: Int, size: Int): ByteArray {
    return if (data.size < 128) {
      decryptCipher.doFinal(data, offset, size)
    } else {
      val buffer = ByteArray(size / 128 * 117 + 11)
      var readPostion = offset
      var decodeIndex = 0

      while (readPostion + 128 < size) {
        decodeIndex += decryptCipher.doFinal(data, readPostion, 128, buffer, decodeIndex)
        readPostion += 128
      }
      decodeIndex += decryptCipher.doFinal(data, readPostion, size - readPostion, buffer, decodeIndex)
      buffer.copyOf(decodeIndex)
    }
  }

  override fun encrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int {
    return encryptCipher.doFinal(data, offset, 128, buffer, bufferOffset)
  }

  override fun decrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int {
    return decryptCipher.doFinal(data, offset, 128, buffer, bufferOffset)
  }

  fun sign(data: ByteArray): ByteArray {
    val signature: Signature = Signature.getInstance("MD5withRSA")
    signature.initSign(privateKey)
    signature.update(data)
    return signature.sign()
  }

  fun verify(data: ByteArray, sign: ByteArray): Boolean {
    val signature = Signature.getInstance("MD5withRSA")
    signature.initVerify(publicKey)
    signature.update(data)
    return signature.verify(sign)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as RSA

    if (publicKey != other.publicKey) return false
    if (privateKey != other.privateKey) return false

    return true
  }

  override fun hashCode(): Int {
    var result = publicKey.hashCode()
    result = 31 * result + (privateKey?.hashCode() ?: 0)
    return result
  }

  class NoPrivateKeyException(message: String? = null) : Exception(message)
}