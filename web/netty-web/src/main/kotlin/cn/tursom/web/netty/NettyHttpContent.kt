package cn.tursom.web.netty

import cn.tursom.core.buf
import cn.tursom.core.bytebuffer.AdvanceByteBuffer
import cn.tursom.web.AdvanceHttpContent
import cn.tursom.web.utils.Chunked
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.*
import io.netty.handler.stream.ChunkedFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.RandomAccessFile
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class NettyHttpContent(
    val ctx: ChannelHandlerContext,
    val msg: FullHttpRequest
) : AdvanceHttpContent {
    override val uri: String by lazy {
        var uri = msg.uri()
        while (uri.contains("//")) {
            uri = uri.replace("//", "/")
        }
        uri
    }
    override val clientIp get() = ctx.channel().remoteAddress()!!
    override val realIp: String = super.realIp
    val httpMethod: HttpMethod get() = msg.method()
    val protocolVersion: HttpVersion get() = msg.protocolVersion()
    val headers: HttpHeaders get() = msg.headers()
    protected val paramMap by lazy { RequestParser.parse(msg) }
    override val cookieMap by lazy { getHeader("Cookie")?.let { decodeCookie(it) } ?: mapOf() }
    override val body = msg.content()?.let { NettyAdvanceByteBuffer(it) }

    val responseMap = HashMap<String, Any>()
    val responseListMap = HashMap<String, ArrayList<Any>>()
    override val responseBody = ByteArrayOutputStream()
    override var responseCode: Int = 200
    override var responseMessage: String? = null
    override val method: String get() = httpMethod.name()
    val chunkedList = ArrayList<AdvanceByteBuffer>()

    override fun getHeader(header: String): String? {
        return headers[header]
    }

    override fun getHeaders(): List<Map.Entry<String, String>> {
        return headers.toList()
    }

    override fun getParam(param: String): String? {
        return paramMap[param]?.get(0)
    }

    override fun getParams(): Map<String, List<String>> {
        return paramMap
    }

    override fun getParams(param: String): List<String>? {
        return paramMap[param]
    }

    override fun addParam(key: String, value: String) {
        if (!paramMap.containsKey(key)) {
            paramMap[key] = ArrayList()
        }
        (paramMap[key] as ArrayList).add(value)
    }

    override fun setResponseHeader(name: String, value: Any) {
        responseMap[name] = value
    }

    override fun addResponseHeader(name: String, value: Any) {
        val list = responseListMap[name] ?: run {
            val newList = ArrayList<Any>()
            responseListMap[name] = newList
            newList
        }
        list.add(value)
    }

    override fun write(message: String) {
        responseBody.write(message.toByteArray())
    }

    override fun write(byte: Byte) {
        responseBody.write(byte.toInt())
    }

    override fun write(bytes: ByteArray, offset: Int, size: Int) {
        responseBody.write(bytes, offset, size)
    }

    override fun write(buffer: AdvanceByteBuffer) {
        buffer.writeTo(responseBody)
    }

    override fun reset() {
        responseBody.reset()
    }

    override fun finish() {
        finish(responseBody.buf, 0, responseBody.size())
    }

    override fun finish(buffer: ByteArray, offset: Int, size: Int) {
        finish(Unpooled.wrappedBuffer(buffer, offset, size))
    }

    override fun finish(buffer: AdvanceByteBuffer) {
        if (buffer is NettyAdvanceByteBuffer) {
            finish(buffer.byteBuf)
        } else {
            super.finish(buffer)
        }
    }

    fun finish(buf: ByteBuf) = finish(buf, HttpResponseStatus.valueOf(responseCode))
    fun finish(buf: ByteBuf, responseCode: HttpResponseStatus) {
        val response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseCode, buf)
        finish(response)
    }

    fun finish(response: FullHttpResponse) {
        val heads = response.headers()
        addHeaders(
            heads, mapOf(
                HttpHeaderNames.CONTENT_TYPE to "${HttpHeaderValues.TEXT_PLAIN}; charset=UTF-8",
                HttpHeaderNames.CONTENT_LENGTH to response.content().readableBytes(),
                HttpHeaderNames.CONNECTION to HttpHeaderValues.KEEP_ALIVE
            )
        )

        ctx.writeAndFlush(response)
    }

    fun addHeaders(heads: HttpHeaders, defaultHeaders: Map<out CharSequence, Any>) {
        responseListMap.forEach { (t, u) ->
            u.forEach {
                heads.add(t, it)
            }
        }

        defaultHeaders.forEach { (t, u) ->
            heads.set(t, u)
        }

        responseMap.forEach { (t, u) ->
            heads.set(t, u)
        }
    }

    override fun writeChunkedHeader() {
        val response = DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        response.status = if (responseMessage != null) HttpResponseStatus(responseCode, responseMessage)
        else HttpResponseStatus.valueOf(responseCode)
        val heads = response.headers()
        addHeaders(
            heads, mapOf(
                HttpHeaderNames.CONTENT_TYPE to "${HttpHeaderValues.TEXT_PLAIN}; charset=UTF-8",
                HttpHeaderNames.CONNECTION to HttpHeaderValues.KEEP_ALIVE,
                HttpHeaderNames.TRANSFER_ENCODING to "chunked"
            )
        )
        ctx.write(response)
    }

    override fun addChunked(buffer: AdvanceByteBuffer) {
        chunkedList.add(buffer)
    }

    override fun finishChunked() {
        val httpChunkWriter = HttpChunkedInput(NettyChunkedByteBuffer(chunkedList))
        ctx.writeAndFlush(httpChunkWriter)
    }

    override fun finishChunked(chunked: Chunked) {
        val httpChunkWriter = HttpChunkedInput(NettyChunkedInput(chunked))
        ctx.writeAndFlush(httpChunkWriter)
    }

    override fun finishFile(file: File, chunkSize: Int) {
        writeChunkedHeader()
        ctx.writeAndFlush(HttpChunkedInput(ChunkedFile(file, chunkSize)))
    }

    override fun finishFile(file: RandomAccessFile, offset: Long, length: Long, chunkSize: Int) {
        writeChunkedHeader()
        ctx.writeAndFlush(HttpChunkedInput(ChunkedFile(file, offset, length, chunkSize)))
    }
}

