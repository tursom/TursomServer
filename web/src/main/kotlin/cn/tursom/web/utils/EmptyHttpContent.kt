package cn.tursom.web.utils

import cn.tursom.core.buffer.ByteBuffer
import cn.tursom.web.HttpContent
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.RandomAccessFile
import java.net.InetSocketAddress
import java.net.SocketAddress

class EmptyHttpContent(
  override val finished: Boolean = true,
  override val uri: String = "/",
  override var responseCode: Int = 200,
  override var responseMessage: String? = null,
  override val body: ByteBuffer? = null,
  override val clientIp: SocketAddress = InetSocketAddress(0),
  override val method: String = "GET",
  override val cookieMap: Map<String, String> = mapOf(),
  override val requestSendFully: Boolean
) : HttpContent {
  override fun getHeader(header: String): String? = null
  override fun getHeaders(header: String): List<String> = listOf()
  override fun getHeaders(): List<Map.Entry<String, String>> = listOf()
  override fun getParam(param: String): String? = null
  override fun getParams(): Map<String, List<String>> = mapOf()
  override fun getParams(param: String): List<String>? = null
  override fun setResponseHeader(name: String, value: Any) {}
  override fun addResponseHeader(name: String, value: Any) {}
  override fun write(message: String) {}
  override fun write(byte: Byte) {}
  override fun write(bytes: ByteArray, offset: Int, size: Int) {}
  override fun write(buffer: ByteBuffer) {}
  override fun reset() {}
  override fun finish() {}
  override fun finish(buffer: ByteArray, offset: Int, size: Int) {}
  override fun finish(code: Int) {}
  override fun finishHtml(code: Int) {}
  override fun finishText(code: Int) {}
  override fun finishJson(code: Int) {}
  override fun writeChunkedHeader() {}
  override fun addChunked(buffer: () -> ByteBuffer) {}
  override fun finishChunked() {}
  override fun finishChunked(chunked: Chunked) {}
  override fun finishFile(file: File, chunkSize: Int) {}
  override fun finishFile(file: RandomAccessFile, offset: Long, length: Long, chunkSize: Int) {}
  override fun addBodyParam(body: ByteBuffer) {}
  override fun waitBody(action: (end: Boolean) -> Unit) {}
}

