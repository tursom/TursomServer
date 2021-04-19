package cn.tursom.http

import cn.tursom.core.isInheritanceFrom
import cn.tursom.core.xml.Xml
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.dom4j.Document
import org.dom4j.Node
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

object XmlConverterFactory : Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, out Node>? {
    return if (type is Class<*> && Document::class.java.isInheritanceFrom(type)) {
      DocumentResponseBodyConverter
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
    return if (type is Class<*> && type.isInheritanceFrom(Node::class.java)) {
      NodeRequestBodyConverter
    } else {
      null
    }
  }

  object DocumentResponseBodyConverter : Converter<ResponseBody, Document> {
    override fun convert(value: ResponseBody): Document {
      return Xml.saxReader.read(value.string().reader())
    }
  }

  object NodeRequestBodyConverter : Converter<Node, RequestBody> {
    override fun convert(value: Node): RequestBody {
      return RequestBody.create(MediaType.parse("text/xml; charset=utf-8"), value.asXML())
    }
  }
}