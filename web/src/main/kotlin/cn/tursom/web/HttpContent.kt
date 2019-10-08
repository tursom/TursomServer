package cn.tursom.web

import cn.tursom.core.buf
import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.core.count
import cn.tursom.core.urlDecode
import cn.tursom.web.utils.CacheControl
import cn.tursom.web.utils.Chunked
import cn.tursom.web.utils.Cookie
import cn.tursom.web.utils.SameSite
import io.netty.handler.codec.DateFormatter
import io.netty.handler.codec.http.cookie.ServerCookieDecoder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.RandomAccessFile
import java.net.SocketAddress
import java.util.*
import kotlin.collections.HashMap

interface HttpContent {
	val uri: String
	var responseCode: Int
	var responseMessage: String?
	val body: AdvanceByteBuffer?
	val clientIp: SocketAddress
	val method: String
	val responseBody: ByteArrayOutputStream
	val cookieMap get() = getHeader("Cookie")?.let { decodeCookie(it) }
	val realIp
		get() = getHeader("X-Forwarded-For") ?: clientIp.toString().let { str ->
			str.substring(1, str.indexOf(':').let { if (it < 1) str.length else it - 1 })
		}

	fun getHeader(header: String): String?
	fun getHeaders(): List<Map.Entry<String, String>>

	fun getParam(param: String): String?
	fun getParams(): Map<String, List<String>>
	fun getParams(param: String): List<String>?

	operator fun get(name: String) = (getHeader(name) ?: getParam(name))?.urlDecode

	fun setResponseHeader(name: String, value: Any)
	fun addResponseHeader(name: String, value: Any)
	operator fun set(name: String, value: Any) = setResponseHeader(name, value)

	fun write(message: String)
	fun write(byte: Byte)
	fun write(bytes: ByteArray, offset: Int = 0, size: Int = 0)
	fun write(buffer: AdvanceByteBuffer)
	fun reset()

	fun finish() = finish(responseBody.buf, 0, responseBody.count)
	fun finish(buffer: ByteArray, offset: Int = 0, size: Int = buffer.size - offset)
	fun finish(buffer: AdvanceByteBuffer) = finish(buffer.array, buffer.readOffset, buffer.readAllSize())
	fun finish(code: Int) = finishHtml(code)

	fun finishHtml(code: Int = responseCode) {
		responseCode = code
		setResponseHeader("content-type", "text/html; charset=UTF-8")
		finish()
	}

	fun finishText(code: Int = responseCode) {
		responseCode = code
		setResponseHeader("content-type", "text/plain; charset=UTF-8")
		finish()
	}

	fun finishJson(code: Int = responseCode) {
		responseCode = code
		setResponseHeader("content-type", "application/json; charset=UTF-8")
		finish()
	}

	fun finishHtml(response: ByteArray, code: Int = responseCode) {
		responseCode = code
		setResponseHeader("content-type", "text/html; charset=UTF-8")
		finish(response)
	}

	fun finishText(response: ByteArray, code: Int = responseCode) {
		responseCode = code
		setResponseHeader("content-type", "text/plain; charset=UTF-8")
		finish(response)
	}

	fun finishJson(response: ByteArray, code: Int = responseCode) {
		responseCode = code
		setResponseHeader("content-type", "application/json; charset=UTF-8")
		finish(response)
	}

	fun finishHtml(response: AdvanceByteBuffer, code: Int = responseCode) {
		responseCode = code
		setResponseHeader("content-type", "text/html; charset=UTF-8")
		finish(response)
	}

	fun finishText(response: AdvanceByteBuffer, code: Int = responseCode) {
		responseCode = code
		setResponseHeader("content-type", "text/plain; charset=UTF-8")
		finish(response)
	}

	fun finishJson(response: AdvanceByteBuffer, code: Int = responseCode) {
		responseCode = code
		setResponseHeader("content-type", "application/json; charset=UTF-8")
		finish(response)
	}

	fun usingCache() = finish(304)

	fun setCacheTag(tag: Any) = setResponseHeader("Etag", tag)
	fun getCacheTag(): String? = getHeader("If-None-Match")

	fun cacheControl(
		cacheControl: CacheControl,
		maxAge: Int? = null,
		mustRevalidate: Boolean = false
	) = setResponseHeader("Cache-Control", "$cacheControl${
	if (maxAge != null && maxAge > 0) ", max-age=$maxAge" else ""}${
	if (mustRevalidate) ", must-revalidate" else ""
	}")

	fun getCookie(name: String): Cookie? = cookieMap?.get(name)

	fun addCookie(
		name: String,
		value: Any,
		maxAge: Int = 0,
		domain: String? = null,
		path: String? = null,
		sameSite: SameSite? = null
	) = addResponseHeader(
		"Set-Cookie",
		"$name=$value${
		if (maxAge > 0) "; Max-Age=$maxAge" else ""}${
		if (domain != null) "; Domain=$domain" else ""}${
		if (path != null) "; Path=$path" else ""}${
		if (sameSite != null) ": SameSite=$sameSite" else ""
		}")

	fun setCookie(
		name: String,
		value: Any,
		maxAge: Int = 0,
		domain: String? = null,
		path: String? = null,
		sameSite: SameSite? = null
	) {
		deleteCookie(name, path ?: "/")
		addCookie(name, value, maxAge, domain, path, sameSite)
	}

	fun deleteCookie(name: String, path: String = "/") =
		addCookie(name, "deleted; expires=Thu, 01 Jan 1970 00:00:00 GMT", path = path)

	fun parseHttpDate(date: CharSequence, start: Int = 0, end: Int = date.length): Date = DateFormatter.parseHttpDate(date, start, end)
	fun format(date: Date): String = DateFormatter.format(date)
	fun append(date: Date, sb: StringBuilder): StringBuilder = DateFormatter.append(date, sb)

	fun decodeCookie(cookie: String): Map<String, Cookie> {
		val cookieMap = HashMap<String, Cookie>()
		ServerCookieDecoder.STRICT.decode(cookie).forEach {
			cookieMap[it.name()] = Cookie(it.name(), it.value(), it.domain(), it.path(), it.maxAge())
		}
		return cookieMap
	}

	fun writeChunkedHeader()
	fun addChunked(buffer: AdvanceByteBuffer)
	fun finishChunked()

	fun finishChunked(chunked: Chunked)

	fun finishFile(file: File, chunkSize: Int = 8192)
	fun finishFile(file: RandomAccessFile, offset: Long = 0, length: Long = file.length() - offset, chunkSize: Int = 8192)

	fun setContextType(type: Any) = setResponseHeader("Content-Type", type)
}