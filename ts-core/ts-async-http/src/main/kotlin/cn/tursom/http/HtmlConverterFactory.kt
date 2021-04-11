package cn.tursom.http

import cn.tursom.core.isInheritanceFrom
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

object HtmlConverterFactory : Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, out Node>? {
    return if (type is Class<*> && Document::class.java.isInheritanceFrom(type)) {
      DocumentResponseBodyConverter(retrofit.baseUrl().uri().toString())
    } else {
      null
    }
  }

  override fun requestBodyConverter(
    type: Type,
    parameterAnnotations: Array<Annotation>,
    methodAnnotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<in Node, RequestBody>? {
    return if (type is Class<*> && type::class.java.isInheritanceFrom(Node::class.java)) {
      NodeRequestBodyConverter
    } else {
      null
    }
  }

  class DocumentResponseBodyConverter(
    private val baseUri: String
  ) : Converter<ResponseBody, Document> {
    override fun convert(value: ResponseBody): Document {
      return Jsoup.parse(value.string(), baseUri)
    }
  }

  object NodeRequestBodyConverter : Converter<Node, RequestBody> {
    override fun convert(value: Node): RequestBody {
      return RequestBody.create(MediaType.parse("text/html; charset=utf-8"), value.outerHtml())
    }
  }
}