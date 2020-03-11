package cn.tursom.mongodb

import cn.tursom.core.Parser
import org.bson.Document

interface BsonFactory<T> {
  val clazz: Class<T>

  fun parse(document: Document) = Parser.parse(document, clazz)!!

  fun convertToBson(entity: Any): Document

  //fun convertToEntity(bson: Document): T
}