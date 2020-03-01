package cn.tursom.core.encrypt

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


@Suppress("unused", "MemberVisibilityCanBePrivate")
class RSA(val publicKey: RSAPublicKey, val privateKey: RSAPrivateKey? = null) : Encrypt {
  val publicKeyEncoded get() = publicKey.encoded!!
  val privateKeyEncoded get() = privateKey?.encoded

  val keySize = publicKey.modulus.bitLength()
  val decryptMaxLen = keySize / 8
  val encryptMaxLen = decryptMaxLen - 11

  val public by lazy {
    if (privateKey == null) {
      this
    } else {
      RSA(publicKey)
    }
  }

  private val encryptCipher = Cipher.getInstance("RSA")!!
  private val decryptCipher = Cipher.getInstance("RSA")!!

  init {
    if (privateKey != null) {
      encryptCipher.init(Cipher.ENCRYPT_MODE, privateKey)
      decryptCipher.init(Cipher.DECRYPT_MODE, privateKey)
    } else {
      encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
      decryptCipher.init(Cipher.DECRYPT_MODE, publicKey)
    }
  }

  constructor(keyPair: KeyPair) : this(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey)

  constructor(keySize: Int = 1024) : this(KeyPairGenerator.getInstance("RSA").let {
    it.initialize(keySize)
    it.generateKeyPair()
  })

  constructor(publicKey: ByteArray) : this(KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicKey)) as RSAPublicKey)

  override fun encrypt(data: ByteArray, offset: Int, size: Int): ByteArray {
    return if (size < encryptMaxLen) {
      encryptCipher.doFinal(data, offset, size)
    } else {
      val buffer = ByteArray(((size - 1) / encryptMaxLen + 1) * decryptMaxLen)
      buffer.copyOf(doFinal(data, offset, size, buffer, encryptCipher, encryptMaxLen))
    }
  }

  override fun decrypt(data: ByteArray, offset: Int, size: Int): ByteArray {
    return if (data.size < decryptMaxLen) {
      decryptCipher.doFinal(data, offset, size)
    } else {
      val buffer = ByteArray(size / decryptMaxLen * encryptMaxLen + 11)
      buffer.copyOf(doFinal(data, offset, size, buffer, decryptCipher, decryptMaxLen))
    }
  }

  override fun encrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int {
    return if (data.size < decryptMaxLen) {
      encryptCipher.doFinal(data, offset, size, buffer, bufferOffset)
    } else {
      doFinal(data, offset, size, buffer, encryptCipher, decryptMaxLen, bufferOffset)
    }
  }

  override fun decrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int {
    return if (data.size < decryptMaxLen) {
      decryptCipher.doFinal(data, offset, size, buffer, bufferOffset)
    } else {
      doFinal(data, offset, size, buffer, decryptCipher, decryptMaxLen, bufferOffset)
    }
  }

  private fun doFinal(
    data: ByteArray,
    offset: Int,
    size: Int,
    buffer: ByteArray,
    cipher: Cipher,
    blockSize: Int,
    bufferOffset: Int = 0
  ): Int {
    var readPosition = offset
    var writeIndex = bufferOffset
    while (readPosition + blockSize < size) {
      writeIndex += cipher.doFinal(data, readPosition, blockSize, buffer, writeIndex)
      readPosition += blockSize
    }
    writeIndex += cipher.doFinal(data, readPosition, size - readPosition, buffer, writeIndex)
    return writeIndex - bufferOffset
  }

  fun sign(data: ByteArray, digest: String = "SHA256"): ByteArray {
    val signature: Signature = Signature.getInstance("${digest}withRSA")
    signature.initSign(privateKey)
    signature.update(data)
    return signature.sign()
  }

  fun verify(data: ByteArray, sign: ByteArray, digest: String = "SHA256"): Boolean {
    val signature = Signature.getInstance("${digest}withRSA")
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
