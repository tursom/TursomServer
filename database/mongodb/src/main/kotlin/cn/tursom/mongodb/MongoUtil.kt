package cn.tursom.mongodb

import cn.tursom.core.isTransient
import cn.tursom.mongodb.annotation.Collection
import cn.tursom.mongodb.annotation.Ignore
import org.bson.Document
import org.bson.conversions.Bson
import java.lang.reflect.Field
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

object MongoUtil {
  fun collectionName(clazz: Class<*>): String {
    return clazz.getAnnotation(Collection::class.java)?.name ?: clazz.simpleName
  }

  fun fieldName(field: Field): String {
    return field.getAnnotation(cn.tursom.mongodb.annotation.Field::class.java)?.name ?: field.name
  }

  fun fieldName(property: KProperty<*>): String {
    return property.findAnnotation<cn.tursom.mongodb.annotation.Field>()?.name
      ?: property.javaField?.getAnnotation(cn.tursom.mongodb.annotation.Field::class.java)?.name
      ?: property.name
  }

  fun convertToBson(entity: Any): Bson {
    val bson = Document()
    entity.javaClass.declaredFields.filter {
      !it.isTransient() && it.getAnnotation(Ignore::class.java) == null
    }.forEach {
      val value = it.get(entity) ?: return@forEach
      bson[MongoUtil.fieldName(it)] = value
    }
    return bson
  }
}