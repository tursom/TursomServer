package cn.tursom.core.encrypt

import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


class RSA : Encrypt {
	val publicKey: RSAPublicKey
	
	private val cipher = Cipher.getInstance("RSA")!!
	
	private val encryptCipher = Cipher.getInstance("RSA")!!
	private val decryptCipher: Cipher?
	
	constructor() {
		val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
		keyPairGenerator.initialize(1024)//512-65536 & 64的倍数
		val keyPair = keyPairGenerator.generateKeyPair()
		publicKey = keyPair.public as RSAPublicKey
		encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
		decryptCipher = Cipher.getInstance("RSA")!!
		decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.private as RSAPrivateKey)
	}
	
	constructor(publicKey: RSAPublicKey) {
		this.publicKey = publicKey
		encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
		decryptCipher = null
	}
	
	constructor(publicKey: ByteArray) : this(KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicKey)) as RSAPublicKey)
	
	override fun encrypt(data: ByteArray, offset: Int, size: Int): ByteArray {
		return if (size < 117)
			encryptCipher.doFinal(data, offset, size)
		else {
			val buffer = ByteArray(size / 117 * 128 + 128)
			var readPosition = offset
			var decodeIndex = 0
			
			while (readPosition + 117 < size) {
				decodeIndex += cipher.doFinal(data, readPosition, 117, buffer, decodeIndex)
				readPosition += 117
			}
			decodeIndex += cipher.doFinal(data, readPosition, size - readPosition, buffer, decodeIndex)
			
			buffer.copyOf(decodeIndex)
		}
	}
	
	override fun decrypt(data: ByteArray, offset: Int, size: Int): ByteArray {
		return if (data.size < 128) {
			decryptCipher!!.doFinal(data, offset, size)
		} else {
			val buffer = ByteArray(size / 128 * 117 + 11)
			var readPostion = offset
			var decodeIndex = 0
			
			while (readPostion + 128 < size) {
				decodeIndex += cipher.doFinal(data, readPostion, 128, buffer, decodeIndex)
				readPostion += 128
			}
			decodeIndex += cipher.doFinal(data, readPostion, size - readPostion, buffer, decodeIndex)
			buffer.copyOf(decodeIndex)
		}
	}
	
	override fun encrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int {
		return encryptCipher.doFinal(data, offset, 128, buffer, bufferOffset)
	}
	
	override fun decrypt(data: ByteArray, buffer: ByteArray, bufferOffset: Int, offset: Int, size: Int): Int {
		return decryptCipher!!.doFinal(data, offset, 128, buffer, bufferOffset)
	}
	
	class NoPrivateKeyException(message: String? = null) : Exception(message)
}