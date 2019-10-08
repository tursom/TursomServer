package cn.tursom.utils

import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object Gzip {
	fun compress(data: ByteArray): ByteArray {
		val out = ByteArrayOutputStream()
		compress(data, out)
		return out.toByteArray()
	}
	
	fun compress(data: ByteArray, out: OutputStream) {
		val gzip = GZIPOutputStream(out)
		gzip.write(data)
		gzip.close()
	}
	
	fun uncompress(bytes: ByteArray): ByteArray {
		return uncompress(ByteArrayInputStream(bytes)).readBytes()
	}
	
	fun uncompress(inputStream: InputStream): InputStream {
		return GZIPInputStream(inputStream)
	}
	
	class GzipBuilder(private val out: OutputStream = ByteArrayOutputStream()) : Closeable {
		private val gzip = GZIPOutputStream(out)
		fun write(byte: Int) = gzip.write(byte)
		fun write(bytes: ByteArray) = gzip.write(bytes)
		fun write(bytes: ByteArray, offset: Int, len: Int) = gzip.write(bytes, offset, len)
		fun get(): ByteArray = (out as ByteArrayOutputStream).toByteArray()
		override fun close() = gzip.close()
	}
}