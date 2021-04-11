package cn.tursom.utils

import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.SocketAddress
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@Suppress("unused", "MemberVisibilityCanBePrivate")
object AsyncHttpRequest {
	val defaultClient: OkHttpClient = OkHttpClient().newBuilder()
		.retryOnConnectionFailure(true)
		.build()
	val socketClient: OkHttpClient = proxyClient()
	val httpProxyClient: OkHttpClient =
        proxyClient(port = 8080, type = Proxy.Type.HTTP)

	fun proxyClient(
		host: String = "127.0.0.1",
		port: Int = 1080,
		type: Proxy.Type = Proxy.Type.SOCKS
	): OkHttpClient = OkHttpClient().newBuilder()
		.proxy(Proxy(type, InetSocketAddress(host, port) as SocketAddress))
		.retryOnConnectionFailure(true)
		.build()

	suspend fun sendRequest(call: Call): Response = suspendCoroutine {
		call.enqueue(object : Callback {
			override fun onFailure(call: Call, e: IOException) {
				it.resumeWithException(e)
			}

			override fun onResponse(call: Call, response: Response) {
				it.resume(response)
			}
		})
	}
	
	suspend fun get(
		url: String,
		param: Map<String, String>? = null,
		headers: Map<String, String>? = null,
		client: OkHttpClient = defaultClient
	): Response {
		val paramSB = StringBuilder()
		param?.forEach {
			paramSB.append("${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}&")
		}
		if (paramSB.isNotEmpty())
			paramSB.deleteCharAt(paramSB.length - 1)
		
		val requestBuilder = Request.Builder().get()
			.url("$url?$paramSB")
		
		headers?.forEach { t, u ->
			requestBuilder.addHeader(t, u)
		}
		
		return sendRequest(
            client.newCall(
                requestBuilder.build()
            )
        )
	}
	
	private suspend fun post(
		url: String,
		body: RequestBody,
		headers: Map<String, String>? = null,
		client: OkHttpClient = defaultClient
	): Response {
		val requestBuilder = Request.Builder()
			.post(body)
			.url(url)
		
		headers?.forEach { t, u ->
			requestBuilder.addHeader(t, u)
		}
		
		return sendRequest(client.newCall(requestBuilder.build()))
	}
	
	suspend fun post(
		url: String,
		param: Map<String, String>,
		headers: Map<String, String>? = null,
		client: OkHttpClient = defaultClient
	): Response {
		val formBuilder = FormBody.Builder()
		param.forEach { (t, u) ->
			formBuilder.add(t, u)
		}
		return post(url, formBuilder.build(), headers, client)
	}
	
	suspend fun post(
		url: String,
		body: String,
		headers: Map<String, String>? = null,
		client: OkHttpClient = defaultClient
	) = post(
        url,
        RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), body),
        headers,
        client
    )
	
	suspend fun post(
		url: String,
		body: File,
		headers: Map<String, String>? = null,
		client: OkHttpClient = defaultClient
	) = post(
        url,
        RequestBody.create(MediaType.parse("application/octet-stream"), body),
        headers,
        client
    )
	
	suspend fun post(
		url: String,
		body: ByteArray,
		headers: Map<String, String>? = null,
		client: OkHttpClient = defaultClient
	) = post(
        url,
        RequestBody.create(MediaType.parse("application/octet-stream"), body),
        headers,
        client
    )
	
	suspend fun getStr(
		url: String,
		param: Map<String, String>? = null,
		headers: Map<String, String>? = null
	): String =
        getStr(url, param, headers, defaultClient)
	
	@Suppress("BlockingMethodInNonBlockingContext")
	suspend fun getStr(
		url: String,
		param: Map<String, String>? = null,
		headers: Map<String, String>? = null,
		client: OkHttpClient
	): String = get(url, param, headers, client).body()!!.string()
	
	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend fun postStr(
		url: String,
		body: RequestBody,
		headers: Map<String, String>? = null,
		client: OkHttpClient
	): String = post(url, body, headers, client).body()!!.string()
	
	suspend fun postStr(
		url: String,
		param: Map<String, String>,
		headers: Map<String, String>? = null
	): String =
        postStr(url, param, headers, defaultClient)
	
	suspend fun postStr(
		url: String,
		param: Map<String, String>,
		headers: Map<String, String>? = null,
		client: OkHttpClient
	): String {
		val formBuilder = FormBody.Builder()
		param.forEach { (t, u) ->
			formBuilder.add(t, u)
		}
		return postStr(url, formBuilder.build(), headers, client)
	}
	
	suspend fun postStr(
		url: String,
		body: String,
		headers: Map<String, String>? = null
	): String =
        postStr(url, body, headers, defaultClient)
	
	suspend fun postStr(
		url: String,
		body: String,
		headers: Map<String, String>? = null,
		client: OkHttpClient
	): String = postStr(
        url,
        RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), body),
        headers,
        client
    )
	
	suspend fun postStr(
		url: String,
		body: File,
		headers: Map<String, String>? = null
	): String =
        postStr(url, body, headers, defaultClient)
	
	suspend fun postStr(
		url: String,
		body: File,
		headers: Map<String, String>? = null,
		client: OkHttpClient
	): String = postStr(
        url,
        RequestBody.create(MediaType.parse("application/octet-stream"), body),
        headers,
        client
    )
	
	suspend fun getByteArray(
		url: String,
		param: Map<String, String>? = null,
		headers: Map<String, String>? = null
	): ByteArray = getByteArray(
        url,
        param,
        headers,
        defaultClient
    )
	
	@Suppress("BlockingMethodInNonBlockingContext")
	suspend fun getByteArray(
		url: String,
		param: Map<String, String>? = null,
		headers: Map<String, String>? = null,
		client: OkHttpClient
	): ByteArray = get(url, param, headers, client).body()!!.bytes()
	
	
	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend fun postByteArray(
		url: String,
		body: RequestBody,
		headers: Map<String, String>? = null,
		client: OkHttpClient
	): ByteArray = post(url, body, headers, client).body()!!.bytes()
	
	
	suspend fun postByteArray(
		url: String,
		param: Map<String, String>,
		headers: Map<String, String>? = null
	): ByteArray = postByteArray(
        url,
        param,
        headers,
        defaultClient
    )
	
	suspend fun postByteArray(
		url: String,
		param: Map<String, String>,
		headers: Map<String, String>? = null,
		client: OkHttpClient
	): ByteArray {
		val formBuilder = FormBody.Builder()
		param.forEach { (t, u) ->
			formBuilder.add(t, u)
		}
		return postByteArray(url, formBuilder.build(), headers, client)
	}
	
	suspend fun postByteArray(
		url: String,
		body: String,
		headers: Map<String, String>? = null
	): ByteArray = postByteArray(
        url,
        body,
        headers,
        defaultClient
    )
	
	suspend fun postByteArray(
		url: String,
		body: String,
		headers: Map<String, String>? = null,
		client: OkHttpClient
	): ByteArray = postByteArray(
        url,
        RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), body),
        headers,
        client
    )
	
	suspend fun postByteArray(
		url: String,
		body: File,
		headers: Map<String, String>? = null
	): ByteArray = postByteArray(
        url,
        body,
        headers,
        defaultClient
    )
	
	suspend fun postByteArray(
		url: String,
		body: File,
		headers: Map<String, String>? = null,
		client: OkHttpClient
	): ByteArray = postByteArray(
        url,
        RequestBody.create(MediaType.parse("application/octet-stream"), body),
        headers,
        client
    )
}
