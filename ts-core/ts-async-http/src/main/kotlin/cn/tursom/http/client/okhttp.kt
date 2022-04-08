@file:Suppress("unused")

package cn.tursom.http.client

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.SocketAddress
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@DslMarker
@Retention(AnnotationRetention.BINARY)
annotation class OkhttpMaker

@OkhttpMaker
object Okhttp : Call.Factory, WebSocket.Factory {
  val direct: OkHttpClient = OkHttpClient().newBuilder()
    .retryOnConnectionFailure(true)
    .build()
  val socket: OkHttpClient = proxy()
  val httpProxy: OkHttpClient = proxy(port = 8080, type = Proxy.Type.HTTP)

  var default: OkHttpClient = direct

  override fun newCall(request: Request): Call = default.newCall(request)
  override fun newWebSocket(request: Request, listener: WebSocketListener): WebSocket =
    default.newWebSocket(request, listener)

  @JvmOverloads
  fun proxy(
    host: String = "127.0.0.1",
    port: Int = 1080,
    type: Proxy.Type = Proxy.Type.SOCKS,
    builder: OkHttpClient.Builder = OkHttpClient().newBuilder(),
  ): OkHttpClient = builder
    .proxy(Proxy(type, InetSocketAddress(host, port) as SocketAddress))
    .retryOnConnectionFailure(true)
    .build()
}

@OkhttpMaker
inline fun <B : Request.Builder> B.url(url: String? = null, builder: HttpUrl.Builder.() -> Unit): B = apply {
  val urlBuilder = url?.toHttpUrl()?.newBuilder() ?: HttpUrl.Builder()

  urlBuilder.builder()
  url(urlBuilder.build())
}

fun <B : Request.Builder> B.addHeaders(headers: Map<String, String>?): B = apply {
  headers?.forEach { (k, v) ->
    addHeader(k, v)
  }
}

inline fun Request.Builder.form(builder: FormBody.Builder.() -> Unit): FormBody =
  FormBody.Builder().build(builder)

fun HttpUrl.Builder.addQueryParameters(params: Map<String, String?>?) {
  params?.forEach { (k, v) ->
    addQueryParameter(k, v)
  }
}

@OkhttpMaker
inline infix fun FormBody.Builder.build(builder: FormBody.Builder.() -> Unit): FormBody {
  val form = FormBody.Builder()
  form.builder()
  return form.build()
}

fun FormBody.Builder.add(forms: Map<String, String>?) = apply {
  forms?.forEach { (k, v) ->
    add(k, v)
  }
}

fun FormBody.Builder.addEncoded(forms: Map<String, String>?) = apply {
  forms?.forEach { (k, v) ->
    addEncoded(k, v)
  }
}

suspend fun Call.sendRequest(): Response = suspendCoroutine {
  enqueue(object : Callback {
    override fun onFailure(call: Call, e: IOException) {
      it.resumeWithException(e)
    }

    override fun onResponse(call: Call, response: Response) {
      it.resume(response)
    }
  })
}

suspend fun Call.str() = sendRequest().body!!.string()
suspend fun Call.bytes() = sendRequest().body!!.bytes()

@OkhttpMaker
inline fun WebSocket.Factory.newWebSocket(listener: WebSocketListener, builder: Request.Builder.() -> Unit): WebSocket =
  newWebSocket(Request.Builder().apply(builder).build(), listener)

@OkhttpMaker
inline fun Call.Factory.newCall(builder: Request.Builder.() -> Unit) =
  newCall(Request.Builder().apply(builder).build())
