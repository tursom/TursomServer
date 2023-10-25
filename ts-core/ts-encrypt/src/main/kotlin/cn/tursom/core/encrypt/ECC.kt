package cn.tursom.core.encrypt

import cn.tursom.core.util.Unsafe
import cn.tursom.core.util.uncheckedCast
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.NullCipher


@Suppress("unused", "MemberVisibilityCanBePrivate")
class ECC(
  publicKey: ECPublicKey,
  privateKey: ECPrivateKey? = null,
  algorithm: String = "EC",
) : AbstractPublicKeyEncrypt(algorithm, publicKey, privateKey) {
  override val decryptMaxLen = Int.MAX_VALUE
  override val encryptMaxLen = Int.MAX_VALUE

  override val public by lazy {
    if (privateKey == null) {
      this
    } else {
      ECC(publicKey)
    }
  }

  constructor(
    keyPair: KeyPair,
    algorithm: String = "EC",
  ) : this(keyPair.public as ECPublicKey, keyPair.private as ECPrivateKey, algorithm)

  constructor(
    keySize: Int = 256,
    spec: AlgorithmParameterSpec,
    algorithm: String = "EC",
  ) : this(KeyPairGenerator.getInstance("EC").let {
    val generator = KeyPairGenerator.getInstance("EC")
    generator.initialize(spec, SecureRandom())
    generator.initialize(keySize)
    generator.generateKeyPair()
  }, algorithm)

  constructor(
    keySize: Int = 256,
    standardCurveLine: String = StandardCurveLine.secp256k1.name.replace('_', ' '),
    algorithm: String = "EC",
  ) : this(
    keySize,
    ECGenParameterSpec(standardCurveLine),
    algorithm,
  )

  constructor(keySize: Int = 256, standardCurveLine: StandardCurveLine, algorithm: String = "EC") : this(
    keySize,
    standardCurveLine.name.replace('_', ' '),
    algorithm,
  )

  constructor(publicKey: ByteArray) : this(
    KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(publicKey)) as ECPublicKey
  )

  override fun signature(digest: String): String = "${digest}withECDSA"

  override val encryptCipher by lazy {
    val cipher = NullCipher()
    cipher.init(Cipher.ENCRYPT_MODE, privateKey ?: publicKey)
    cipher
  }
  override val decryptCipher by lazy {
    val cipher = NullCipher()
    cipher.init(Cipher.DECRYPT_MODE, privateKey ?: publicKey)
    cipher
  }

  @Suppress("EnumEntryName", "SpellCheckingInspection")
  enum class StandardCurveLine {
    secp224r1, `NIST_B-233`, secp160r1, secp160r2, `NIST_K-233`, sect163r2, secp128r1, sect163r1, `NIST_P-256`,
    sect409r1, `NIST_B-163`, `NIST_B-283`, secp128r2, brainpoolP192r1, secp192r1, brainpoolP256r1, `NIST_K-283`,
    secp256r1, `NIST_P-384`, sect113r2, sect163k1, `NIST_K-163`, `NIST_B-409`, secp224k1, sect239k1, sect193r2,
    `NIST_K-409`, secp112r2, sect113r1, brainpoolP320r1, secp112r1, secp160k1, `NIST_P-224`, sect193r1, sect233k1,
    sect571r1, `NIST_P-192`, sect409k1, `NIST_B-571`, brainpoolP224r1, sect233r1, sect571k1, brainpoolP160r1,
    `NIST_K-571`, secp256k1, secp192k1, sect283k1, sect283r1, secp384r1, secp521r1, sect131r1, sect131r2,
    brainpoolP384r1, brainpoolP512r1, `NIST_P-521`
  }

  companion object {
    val standardCurveLineSet by lazy {
      try {
        Unsafe {
          Class.forName("sun.security.ec.CurveDB").getField("nameMap").uncheckedCast<Map<String, Any>>().keys
        }
      } catch (e: Throwable) {
        emptySet()
      }
    }
  }
}