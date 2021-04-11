package cn.tursom.core.encrypt

import cn.tursom.core.toHexString
import cn.tursom.core.toUTF8String
import java.security.*
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import kotlin.experimental.xor
import kotlin.math.min
import kotlin.random.Random

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class AbstractPublicKeyEncrypt(
  val algorithm: String,
  final override val publicKey: PublicKey,
  final override val privateKey: PrivateKey? = null,
  val modeOfOperation: BlockCipherModeOfOperation = BlockCipherModeOfOperation.ECB,
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

  private val blockCipher: Encrypt = when (modeOfOperation) {
    BlockCipherModeOfOperation.ECB -> ECBBlockCipher()
    BlockCipherModeOfOperation.CBC -> CBCBlockCipher()
    else -> TODO()
  }

  override var encryptInitVector: ByteArray?
    get() = blockCipher.encryptInitVector
    set(value) {
      blockCipher.encryptInitVector = value
    }
  override var decryptInitVector: ByteArray?
    get() = blockCipher.decryptInitVector
    set(value) {
      blockCipher.decryptInitVector = value
    }

  constructor(
    algorithm: String,
    keyPair: KeyPair,
    modeOfOperation: BlockCipherModeOfOperation = BlockCipherModeOfOperation.ECB,
  ) : this(algorithm, keyPair.public as PublicKey, keyPair.private as PrivateKey, modeOfOperation = modeOfOperation)

  constructor(
    algorithm: String,
    keySize: Int = 1024,
    modeOfOperation: BlockCipherModeOfOperation = BlockCipherModeOfOperation.ECB,
  ) : this(
    algorithm,
    KeyPairGenerator.getInstance(algorithm).let {
      it.initialize(keySize)
      it.generateKeyPair()
    },
    modeOfOperation = modeOfOperation
  )

  constructor(
    algorithm: String,
    publicKey: ByteArray,
    modeOfOperation: BlockCipherModeOfOperation = BlockCipherModeOfOperation.ECB,
  ) : this(
    algorithm,
    KeyFactory.getInstance(algorithm).generatePublic(X509EncodedKeySpec(publicKey)) as PublicKey,
    modeOfOperation = modeOfOperation
  )

  override fun encrypt(data: ByteArray, offset: Int, size: Int): ByteArray = blockCipher.encrypt(data, offset, size)
  override fun decrypt(data: ByteArray, offset: Int, size: Int): ByteArray = blockCipher.decrypt(data, offset, size)
  override fun encrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int =
    blockCipher.encrypt(data, buffer, bufferOffset, offset, size)

  override fun decrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int =
    blockCipher.decrypt(data, buffer, bufferOffset, offset, size)

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

  protected inner class ECBBlockCipher : Encrypt {
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
      bufferOffset: Int = 0,
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
  }

  protected inner class CBCBlockCipher : Encrypt {
    override var encryptInitVector: ByteArray? = Random.nextBytes(encryptMaxLen)
      set(value) {
        value ?: return
        field = value
        encBuf = value
      }
    override var decryptInitVector: ByteArray? = null
      set(value) {
        field = value
        decBuf = value
      }

    private var encBuf = encryptInitVector!!
    private var decBuf: ByteArray? = decryptInitVector

    override fun encrypt(data: ByteArray, offset: Int, size: Int): ByteArray {
      val buffer = ByteArray(((size - 1) / encryptMaxLen + 1) * decryptMaxLen)
      //return buffer.copyOf(encrypt(data, buffer, 0, offset, size))
      encrypt(data, buffer, 0, offset, size)
      return buffer
    }

    override fun encrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int {
      var end = offset
      var start: Int
      var writeIndex = bufferOffset
      do {
        start = end
        end += encryptMaxLen
        end = min(data.size, end)
        (0 until end - start).forEach { index ->
          encBuf[index] = encBuf[index] xor data[start + index]
        }
        writeIndex += encryptCipher.doFinal(encBuf, 0, encBuf.size, buffer, writeIndex)
        //println("${data.size} $start->$end $writeIndex")
      } while (end < offset + size)
      return writeIndex - bufferOffset
    }

    override fun decrypt(data: ByteArray, offset: Int, size: Int): ByteArray {
      val decryptInitVector = decBuf!!
      var start: Int
      var end = offset
      val buffer = ByteArray(((size - 1) / decryptMaxLen + 1) * encryptMaxLen + 11)
      var writeIndex = 0
      do {
        start = end
        end += decryptMaxLen
        end = min(data.size, end)
        println("${data.size}, $start->$end, ${buffer.size}, $writeIndex")
        val writeIndexBefore = writeIndex
        writeIndex += decryptCipher.doFinal(data, start, end - start, buffer, writeIndex)
        if (start == 0) {
          repeat(encryptMaxLen) {
            buffer[it] = buffer[it] xor decryptInitVector[it]
          }
        } else {
          repeat(writeIndex - writeIndexBefore) {
            buffer[writeIndexBefore + it] = buffer[writeIndexBefore + it] xor data[start + it]
          }
        }
      } while (end < offset + size)
      decBuf = buffer.copyOfRange(buffer.size - encryptMaxLen, buffer.size)
      return buffer.copyOf(writeIndex)
    }

    override fun decrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int {
      TODO("Not yet implemented")
    }

    //private fun doFinal(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int, cipher: Cipher): Int {
    //  var start = offset
    //  var end = offset
    //  var writeIndex = bufferOffset
    //  do {
    //    end += decryptMaxLen
    //    end = min(data.size, end)
    //    encBuf.indices.forEach { index ->
    //      encBuf[index] = encBuf[index] xor data[start + index]
    //    }
    //    writeIndex += cipher.doFinal(encBuf, 0, encBuf.size, buffer, writeIndex)
    //    start += decryptMaxLen
    //  } while (end < offset + size)
    //  return writeIndex - bufferOffset
    //}
  }

  companion object {
    private val random = Random(System.currentTimeMillis())
  }
}


fun main() {
  val source = "HelloWorld".repeat(100).toByteArray()
  val rsa = RSA()
  val decodeRsa = rsa.public
  decodeRsa.decryptInitVector = rsa.encryptInitVector
  val encrypt = rsa.encrypt(source)
  //println(encrypt.toHexString())
  println(decodeRsa.decrypt(encrypt).toUTF8String())
}