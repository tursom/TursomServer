package cn.tursom.utils

import cn.tursom.core.base64
import cn.tursom.core.base64decode
import cn.tursom.core.digest
import cn.tursom.core.toHexString
import java.lang.Exception
import kotlin.experimental.xor


open class TokenUtil {
  @Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
  enum class DigestType(val digest: String) {
    MD5("MD5"), SHA256("SHA-256"), SHA512("SHA-512");

    val digestBase64: String = digest.base64()
  }

  fun <T> generate(secretKey: String, data: T, timeout: Long = 30 * 60 * 1000, type: DigestType = DigestType.MD5): String {
    val head = type.digestBase64
    val body = toJson(TokenBody(System.currentTimeMillis(), timeout, data)).base64()
    val encryptSource = "$head.$body".toByteArray()
    val encrypt = encrypt(secretKey, encryptSource, type.digest)
    return "$head.$body.$encrypt"
  }

  @Throws(TokenException::class)
  fun <T : Any> decode(secretKey: String, token: String, dataClazz: Class<T>): T {
    val splitToken = token.split(".")
    if (splitToken.size != 3) {
      throw WrongTokenSyntaxException()
    }
    val signature = encrypt(secretKey, "${splitToken[0]}.${splitToken[1]}".toByteArray(), splitToken[0].base64decode())
    if (signature != splitToken[2]) {
      throw WrongSignatureException()
    }
    val decode = fromJson<TokenBody<T>>(splitToken[1].base64decode())
    if (decode.tim + decode.exp < System.currentTimeMillis()) {
      throw TokenTimeoutException()
    }
    return fromJson(toJson(decode.dat!!), dataClazz)
  }

  open fun encrypt(secretKey: String, encryptSource: ByteArray, type: String): String {
    val inner = secretKey.toByteArray().digest(type)!!
    encryptSource.forEachIndexed { index, _ ->
      encryptSource[index] = encryptSource[index] xor inner[index % inner.size]
    }
    val digest1 = encryptSource.digest(type)!!
    digest1.forEachIndexed { index, _ ->
      digest1[index] = digest1[index] xor inner[index % inner.size]
    }
    return digest1.digest(type)!!.toHexString()!!
  }

  open fun toJson(bean: Any): String = gson.toJson(bean)
  open fun <T> fromJson(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)

  private inline fun <reified T : Any> fromJson(json: String): T = fromJson(json, T::class.java)

  data class TokenBody<T>(val tim: Long = System.currentTimeMillis(), val exp: Long = 0L, val dat: T? = null)

  open class TokenException : Exception()
  class WrongTokenSyntaxException : TokenException()
  class WrongSignatureException : TokenException()
  class TokenTimeoutException : TokenException()

  companion object {
    @JvmStatic
    val instance = TokenUtil()
  }
}
