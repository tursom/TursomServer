package cn.tursom.web

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.urlDecode
import cn.tursom.web.utils.CacheControl
import cn.tursom.web.utils.Chunked
import cn.tursom.web.utils.Cookie
import cn.tursom.web.utils.SameSite
import java.io.File
import java.io.RandomAccessFile
import java.net.SocketAddress

interface HttpContent {
  val uri: String
  var responseCode: Int
  var responseMessage: String?
  val body: ByteBuffer?
  val clientIp: SocketAddress
  val method: String
  //val responseBody: ByteArrayOutputStream
  val cookieMap: Map<String, Cookie>
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
  fun write(buffer: ByteBuffer)
  fun reset()

  //fun finish() = finish(responseBody.buf, 0, responseBody.count)

  fun finish()
  fun finish(buffer: ByteArray, offset: Int = 0, size: Int = buffer.size - offset)
  fun finish(buffer: ByteBuffer) {
    if (buffer.hasArray) {
      finish(buffer.array, buffer.readOffset, buffer.readAllSize())
    } else {
      write(buffer)
      finish()
    }
    buffer.close()
  }

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

  fun finishHtml(response: ByteBuffer, code: Int = responseCode) {
    responseCode = code
    setResponseHeader("content-type", "text/html; charset=UTF-8")
    finish(response)
  }

  fun finishText(response: ByteBuffer, code: Int = responseCode) {
    responseCode = code
    setResponseHeader("content-type", "text/plain; charset=UTF-8")
    finish(response)
  }

  fun finishJson(response: ByteBuffer, code: Int = responseCode) {
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
  ) = setResponseHeader(
    "Cache-Control", "$cacheControl${
  if (maxAge != null && maxAge > 0) ", max-age=$maxAge" else ""}${
  if (mustRevalidate) ", must-revalidate" else ""
  }"
  )

  fun getCookie(name: String): Cookie? = cookieMap[name]

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
    }"
  )

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

  fun writeChunkedHeader()
  fun addChunked(buffer: ByteBuffer)
  fun finishChunked()

  fun finishChunked(chunked: Chunked)

  fun finishFile(file: File, chunkSize: Int = 8192)
  fun finishFile(
    file: RandomAccessFile,
    offset: Long = 0,
    length: Long = file.length() - offset,
    chunkSize: Int = 8192
  )

  fun setContextType(type: Any) = setResponseHeader("Content-Type", type)

  fun jump(url: String) = temporaryMoved(url)
  fun moved(url: String) = permanentlyMoved(url)

  fun permanentlyMoved(url: String) {
    setResponseHeader("Location", url)
    finish(301)
  }

  fun temporaryMoved(url: String) {
    noStore()
    setResponseHeader("Location", url)
    finish(302)
  }

  fun noCache() = cacheControl(CacheControl.NoCache)
  fun noStore() = cacheControl(CacheControl.NoStore)

  fun finish(msg: String) {
    write(msg)
    finish()
  }
}