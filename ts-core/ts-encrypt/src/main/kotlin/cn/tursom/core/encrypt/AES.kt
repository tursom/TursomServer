package cn.tursom.core.encrypt

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Suppress("unused")
class AES(
  @Suppress("CanBeParameter") val secKey: SecretKey
) : Encrypt {
  private val decryptCipher = Cipher.getInstance("AES")!!
  private val encryptCipher = Cipher.getInstance("AES")!!

  init {
    encryptCipher.init(Cipher.ENCRYPT_MODE, secKey)
    decryptCipher.init(Cipher.DECRYPT_MODE, secKey)
  }

  constructor() : this(defaultGenerator.generateKey())

  constructor(keySize: Int) : this(KeyGenerator.getInstance("AES").let {
    it.init(keySize)
    it.generateKey()
  })

  constructor(key: ByteArray, keySize: Int = key.size, offset: Int = 0) : this(
    SecretKeySpec(
      key,
      offset,
      keySize,
      "AES"
    )
  )

  override fun encrypt(data: ByteArray, offset: Int, size: Int): ByteArray {
    return encryptCipher.doFinal(data, offset, size)
  }

  override fun decrypt(data: ByteArray, offset: Int, size: Int): ByteArray {
    return decryptCipher.doFinal(data, offset, size)
  }

  override fun encrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int {
    return encryptCipher.doFinal(data, offset, size, buffer, bufferOffset)
  }

  override fun decrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int {
    return decryptCipher.doFinal(data, offset, size, buffer, bufferOffset)
  }

  companion object {
    private val generator128 = KeyGenerator.getInstance("AES")
    private val generator192 = KeyGenerator.getInstance("AES")
    private val generator256 = KeyGenerator.getInstance("AES")

    private val defaultGenerator = generator256

    init {
      generator128.init(128)
      generator192.init(192)
      generator256.init(256)
    }

    fun get128() = AES(generator128.generateKey())
    fun get192() = AES(generator192.generateKey())
    fun get256() = AES(generator256.generateKey())
  }
}