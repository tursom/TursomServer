package cn.tursom.core.encrypt

import cn.tursom.core.Unsafe
import cn.tursom.core.cast
import sun.security.ec.CurveDB
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec


@Suppress("unused", "MemberVisibilityCanBePrivate")
class ECC(
  publicKey: ECPublicKey,
  privateKey: ECPrivateKey? = null
) : AbstractPublicKeyEncrypt("EC", publicKey, privateKey) {

  override val decryptMaxLen = Int.MAX_VALUE
  override val encryptMaxLen = Int.MAX_VALUE

  override val public by lazy {
    if (privateKey == null) {
      this
    } else {
      ECC(publicKey)
    }
  }

  constructor(keyPair: KeyPair) : this(keyPair.public as ECPublicKey, keyPair.private as ECPrivateKey)
  constructor(keySize: Int = 256, spec: AlgorithmParameterSpec) : this(KeyPairGenerator.getInstance("EC").let {
    val generator = KeyPairGenerator.getInstance("EC")
    generator.initialize(spec, SecureRandom())
    generator.initialize(keySize)
    generator.generateKeyPair()
  })

  constructor(keySize: Int = 256, standardCurveLine: String = StandardCurveLine.secp256k1.name) : this(keySize, ECGenParameterSpec(standardCurveLine))
  constructor(keySize: Int = 256, standardCurveLine: StandardCurveLine) : this(keySize, standardCurveLine.name)
  constructor(publicKey: ByteArray) : this(KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(publicKey)) as ECPublicKey)

  override fun signature(digest: String): String = "${digest}withECDSA"

  @Suppress("EnumEntryName", "SpellCheckingInspection")
  enum class StandardCurveLine {
    secp224r1, `NIST B-233`, secp160r1, secp160r2, `NIST K-233`, sect163r2, secp128r1, sect163r1, `NIST P-256`,
    sect409r1, `NIST B-163`, `NIST B-283`, secp128r2, brainpoolP192r1, secp192r1, brainpoolP256r1, `NIST K-283`,
    secp256r1, `NIST P-384`, sect113r2, sect163k1, `NIST K-163`, `NIST B-409`, secp224k1, sect239k1, sect193r2,
    `NIST K-409`, secp112r2, sect113r1, brainpoolP320r1, secp112r1, secp160k1, `NIST P-224`, sect193r1, sect233k1,
    sect571r1, `NIST P-192`, sect409k1, `NIST B-571`, brainpoolP224r1, sect233r1, sect571k1, brainpoolP160r1,
    `NIST K-571`, secp256k1, secp192k1, sect283k1, sect283r1, secp384r1, secp521r1, sect131r1, sect131r2,
    brainpoolP384r1, brainpoolP512r1, `NIST P-521`
  }

  companion object {
    val standardCurveLineSet by lazy {
      Unsafe {
        CurveDB::class.java["nameMap"].cast<Map<String, Any>>().keys
      }
    }
  }
}