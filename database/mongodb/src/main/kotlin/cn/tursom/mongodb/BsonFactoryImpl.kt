package cn.tursom.mongodb

import cn.tursom.core.Unsafe
import cn.tursom.core.cast
import cn.tursom.core.isStatic
import cn.tursom.core.isTransient
import cn.tursom.mongodb.annotation.Ignore
import org.bson.Document

open class BsonFactoryImpl<T>(final override val clazz: Class<T>) : BsonFactory<T> {
  private val fields = clazz.declaredFields.filter {
    it.isAccessible = true
    !it.isStatic() && !it.isTransient() && it.getAnnotation(Ignore::class.java) == null
  }

  override fun convertToBson(entity: Any): Document {
    val bson = Document()
    fields.forEach {
      MongoUtil.injectValue(bson, it.get(entity) ?: return@forEach, it)
    }
    return bson
  }
}