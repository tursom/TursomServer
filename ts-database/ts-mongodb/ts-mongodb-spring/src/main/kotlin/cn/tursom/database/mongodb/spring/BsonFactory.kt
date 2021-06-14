package cn.tursom.database.mongodb.spring

import cn.tursom.core.Parser
import org.bson.Document

interface BsonFactory<T> {
  val clazz: Class<T>

  fun parse(document: Document) = Parser.parse(document, clazz)!!

  fun convertToBson(entity: T): Document

  //fun convertToEntity(bson: Document): T

  companion object {
    operator fun <T> get(clazz: Class<T>): BsonFactory<T> {
      return BsonFactoryImpl(clazz)
    }

    inline fun <reified T : Any?> get(): BsonFactory<T> {
      return get(T::class.java)
    }
  }
}