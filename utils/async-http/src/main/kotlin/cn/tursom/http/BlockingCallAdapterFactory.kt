package cn.tursom.http

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

object BlockingCallAdapterFactory : CallAdapter.Factory() {
  override fun get(
    returnType: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): CallAdapter<*, *>? {
    if (annotations.any { it is retrofit2.SkipCallbackExecutor }) return null
    return object : CallAdapter<Any, Any> {
      override fun responseType(): Type = returnType
      override fun adapt(call: Call<Any>): Any? = call.execute().body()
    }
  }
}