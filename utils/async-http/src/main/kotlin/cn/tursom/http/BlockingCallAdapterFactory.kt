package cn.tursom.http

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture

object BlockingCallAdapterFactory : CallAdapter.Factory() {
  override fun get(
    returnType: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): CallAdapter<out Any?, out Any?>? {
    if (getRawType(returnType) == Call::class.java) return null
    if (getRawType(returnType) == CompletableFuture::class.java) return null
    if (annotations.any { it is retrofit2.SkipCallbackExecutor }) return null
    return object : CallAdapter<Any?, Any?> {
      override fun responseType(): Type = returnType
      override fun adapt(call: Call<Any?>): Any? = call.execute().body()
    }
  }
}