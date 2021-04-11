package cn.tursom.http

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

object StringConverterFactory : Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *>? {
    return if (type == String::class.java) {
      StringResponseBodyConverter
    } else {
      null
    }
  }

  override fun requestBodyConverter(
    type: Type,
    parameterAnnotations: Array<Annotation>,
    methodAnnotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<*, RequestBody>? {
    return if (type == String::class.java) {
      StringRequestBodyConverter
    } else {
      null
    }
  }

  object StringResponseBodyConverter : Converter<ResponseBody, String> {
    override fun convert(value: ResponseBody): String? {
      return value.string()
    }
  }

  object StringRequestBodyConverter : Converter<String, RequestBody> {
    override fun convert(value: String): RequestBody {
      return RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), value)
    }
  }
}