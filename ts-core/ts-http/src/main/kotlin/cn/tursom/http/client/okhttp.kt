@file:Suppress("unused")

package cn.tursom.http.client

import cn.tursom.core.util.Utils.gson
import cn.tursom.core.util.fromJson
import cn.tursom.core.util.fromJsonTyped
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.SocketAddress

@DslMarker
@Retention(AnnotationRetention.BINARY)
annotation class OkhttpMaker

@OkhttpMaker
object Okhttp : Call.Factory, WebSocket.Factory {
  val direct: OkHttpClient = OkHttpClient().newBuilder()
    .retryOnConnectionFailure(true)
    .build()
  val socket: OkHttpClient = proxy()

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
inline fun <B : Request.Builder> B.url(
  url: String? = null,
  builder: HttpUrl.Builder.() -> Unit,
): B = apply {
  val urlBuilder = url?.toHttpUrl()?.newBuilder() ?: HttpUrl.Builder()

  urlBuilder.builder()
  url(urlBuilder.build())
}

fun <B : Request.Builder> B.addHeaders(headers: Map<String, String>?): B = apply {
  headers?.forEach { (k, v) ->
    addHeader(k, v)
  }
}

@OkhttpMaker
inline fun form(builder: FormBody.Builder.() -> Unit): FormBody =
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

fun Call.str() = execute().body!!.string()
fun Call.bytes() = execute().body!!.bytes()
inline fun <reified T : Any> Call.json(): T = execute().body!!.json<T>()
inline fun <reified T : Any> Call.jsonTyped(): T = execute().body!!.jsonTyped<T>()

@OkhttpMaker
inline fun WebSocket.Factory.newWebSocket(
  listener: WebSocketListener,
  builder: Request.Builder.() -> Unit,
): WebSocket = newWebSocket(Request.Builder().apply(builder).build(), listener)

@OkhttpMaker
inline fun Call.Factory.newCall(builder: Request.Builder.() -> Unit) =
  newCall(Request.Builder().apply(builder).build())

inline fun <reified T : Any> ResponseBody.json(): T = gson.fromJson<T>(string())
inline fun <reified T : Any> ResponseBody.jsonTyped(): T = gson.fromJsonTyped<T>(string())
