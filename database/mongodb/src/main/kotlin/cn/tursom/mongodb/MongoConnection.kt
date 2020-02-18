package cn.tursom.mongodb

import cn.tursom.mongodb.annotation.Collection
import com.mongodb.MongoClient

interface MongoConnection {
  val client: MongoClient

  fun <T> createTemplate(clazz: Class<T>): MongoTemplate<T>

  companion object {
    fun collectionName(clazz: Class<*>): String {
      return clazz.getAnnotation(Collection::class.java)?.name ?: clazz.simpleName
    }
  }
}