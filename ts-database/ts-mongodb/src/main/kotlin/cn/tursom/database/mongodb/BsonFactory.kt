package cn.tursom.database.mongodb

import cn.tursom.core.reflect.Parser
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

    fun convertToBson(entity: Any): Document {
      return get(entity.javaClass).convertToBson(entity)
    }
  }
}