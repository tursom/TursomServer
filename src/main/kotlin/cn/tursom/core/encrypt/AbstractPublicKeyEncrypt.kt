package cn.tursom.core.encrypt

import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class AbstractPublicKeyEncrypt(
  val algorithm: String,
  final override val publicKey: PublicKey,
  final override val privateKey: PrivateKey? = null
) : PublicKeyEncrypt {
  val publicKeyEncoded get() = publicKey.encoded!!
  val privateKeyEncoded get() = privateKey?.encoded

  abstract val decryptMaxLen: Int
  abstract val encryptMaxLen: Int

  protected open val cipherAlgorithm = algorithm

  protected open val encryptCipher by lazy {
    val cipher = Cipher.getInstance(cipherAlgorithm)!!
    cipher.init(Cipher.ENCRYPT_MODE, privateKey ?: publicKey)
    cipher
  }
  protected open val decryptCipher by lazy {
    val cipher = Cipher.getInstance(cipherAlgorithm)!!
    cipher.init(Cipher.DECRYPT_MODE, privateKey ?: publicKey)
    cipher
  }

  open val public = if (privateKey == null) {
    @Suppress("LeakingThis")
    this
  } else object : AbstractPublicKeyEncrypt(algorithm, publicKey) {
    override val decryptMaxLen: Int get() = this@AbstractPublicKeyEncrypt.decryptMaxLen
    override val encryptMaxLen: Int get() = this@AbstractPublicKeyEncrypt.encryptMaxLen
    override val cipherAlgorithm: String get() = this@AbstractPublicKeyEncrypt.cipherAlgorithm
    override fun signature(digest: String): String = this@AbstractPublicKeyEncrypt.signature(digest)
  }

  constructor(algorithm: String, keyPair: KeyPair) : this(algorithm, keyPair.public as PublicKey, keyPair.private as PrivateKey)

  constructor(algorithm: String, keySize: Int = 1024) : this(algorithm, KeyPairGenerator.getInstance(algorithm).let {
    it.initialize(keySize)
    it.generateKeyPair()
  })

  constructor(algorithm: String, publicKey: ByteArray) : this(algorithm, KeyFactory.getInstance(algorithm).generatePublic(X509EncodedKeySpec(publicKey)) as PublicKey)

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

  protected open fun signature(digest: String) = "${digest}with$algorithm"

  override fun sign(data: ByteArray, digest: String): ByteArray {
    val signature: Signature = Signature.getInstance(signature(digest))
    signature.initSign(privateKey)
    signature.update(data)
    return signature.sign()
  }

  override fun verify(data: ByteArray, sign: ByteArray, digest: String): Boolean {
    val signature = Signature.getInstance(signature(digest))
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
}