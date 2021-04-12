package cn.tursom.database.mongodb.spring

import cn.tursom.core.isStatic
import cn.tursom.core.isTransient
import org.bson.Document

open class BsonFactoryImpl<T>(final override val clazz: Class<T>) : BsonFactory<T> {
  private val fields = clazz.declaredFields.filter {
    it.isAccessible = true
    !it.isStatic() && !it.isTransient()
  }

  override fun convertToBson(entity: T): Document {
    val bson = Document()
    fields.forEach {
      MongoUtil.injectValue(bson, it.get(entity) ?: return@forEach, it)
    }
    return bson
  }

  override fun toString(): String {
    return "BsonFactoryImpl(clazz=$clazz, fields=$fields)"
  }
}