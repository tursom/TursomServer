package cn.tursom.web

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.core.urlDecode
import cn.tursom.web.utils.Chunked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.RandomAccessFile
import java.net.SocketAddress

interface HttpContent : ResponseHeaderAdapter, RequestHeaderAdapter {
  val requestSendFully: Boolean
  val finished: Boolean
  val uri: String
  var responseCode: Int
  var responseMessage: String?
  val body: ByteBuffer?
  val remoteAddress: SocketAddress
  val method: String
  val realIp
    get() = getHeader("X-Forwarded-For") ?: remoteAddress.toString().let { str ->
      str.substring(1, str.indexOf(':').let { if (it < 1) str.length else it - 1 })
    }

  fun peekBody(): ByteBuffer?
  fun waitBody(action: (end: Boolean) -> Unit = { addBodyParam() })
  fun addBodyParam(body: ByteBuffer)
  fun addBodyParam() {
    addBodyParam(body ?: return)
  }

  fun getParam(param: String): String? = getParams(param)?.firstOrNull()
  fun getParams(): Map<String, List<String>>
  fun getParams(param: String): List<String>?

  operator fun get(name: String) = (getHeader(name) ?: getParam(name))?.urlDecode
  operator fun set(name: String, value: Any) = setResponseHeader(name, value)

  fun write(message: String)
  fun write(byte: Byte)
  fun write(bytes: ByteArray, offset: Int = 0, size: Int = bytes.size - offset)
  fun write(buffer: ByteBuffer)
  fun reset()

  fun finish()
  fun finish(buffer: ByteArray, offset: Int = 0, size: Int = buffer.size - offset)
  fun finish(buffer: ByteBuffer) {
    log?.trace("buffer {}", buffer)
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
    log?.trace("finishHtml {}", code)
    responseHtml()
    responseCode = code
    finish()
  }

  fun finishText(code: Int = responseCode) {
    log?.trace("finishText {}", code)
    responseText()
    responseCode = code
    finish()
  }

  fun finishJson(code: Int = responseCode) {
    log?.trace("finishJson {}", code)
    responseJson()
    responseCode = code
    finish()
  }

  fun finishHtml(response: ByteArray, code: Int = responseCode) {
    log?.trace("finishHtml {}: {}", code, response)
    responseHtml()
    responseCode = code
    finish(response)
  }

  fun finishText(response: ByteArray, code: Int = responseCode) {
    log?.trace("finishText {}: {}", code, response)
    responseText()
    responseCode = code
    finish(response)
  }

  fun finishJson(response: ByteArray, code: Int = responseCode) {
    log?.trace("finishJson {}: {}", code, response)
    responseJson()
    responseCode = code
    finish(response)
  }

  fun finishHtml(response: ByteBuffer, code: Int = responseCode) {
    if (log.traceEnabled) {
      log?.trace("finishHtml {}: {}", code, response)
    }
    responseHtml()
    responseCode = code
    finish(response)
  }

  fun finishText(response: ByteBuffer, code: Int = responseCode) {
    if (log.traceEnabled) {
      log?.trace("finishText {}: {}", code, response)
    }
    responseText()
    responseCode = code
    finish(response)
  }

  fun finishJson(response: ByteBuffer, code: Int = responseCode) {
    if (log.traceEnabled) {
      log?.trace("finishJson {}: {}", code, response)
    }
    responseJson()
    responseCode = code
    finish(response)
  }

  fun usingCache() = finish(304)

  fun deleteCookie(name: String, path: String = "/") =
    addCookie(name, "deleted; expires=Thu, 01 Jan 1970 00:00:00 GMT", path = path)

  fun writeChunkedHeader()
  fun addChunked(buffer: ByteBuffer) = addChunked { buffer }
  fun addChunked(buffer: () -> ByteBuffer)
  fun finishChunked()
  fun finishChunked(chunked: Chunked)

  fun finishFile(file: File, chunkSize: Int = 8192)
  fun finishFile(
    file: RandomAccessFile,
    offset: Long = 0,
    length: Long = file.length() - offset,
    chunkSize: Int = 8192
  )

  fun jump(url: String) = temporaryMoved(url)
  fun moved(url: String) = permanentlyMoved(url)

  fun permanentlyMoved(url: String) {
    if (log.traceEnabled) {
      log?.trace("permanentlyMoved {}", url)
    }
    setResponseHeader("Location", url)
    finish(301)
  }

  fun temporaryMoved(url: String) {
    if (log.traceEnabled) {
      log?.trace("temporaryMoved {}", url)
    }
    noStore()
    setResponseHeader("Location", url)
    finish(302)
  }

  fun finish(msg: String) {
    if (log.traceEnabled) {
      log?.trace("finish {}", msg)
    }
    write(msg)
    finish()
  }

  companion object {
    private val log = try {
      LoggerFactory.getLogger(HttpContent::class.java)
    } catch (e: Throwable) {
      null
    }

    private inline val Logger?.traceEnabled get() = this?.isTraceEnabled ?: false
  }
}