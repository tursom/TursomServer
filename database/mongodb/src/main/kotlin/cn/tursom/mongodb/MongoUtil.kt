package cn.tursom.mongodb

import cn.tursom.core.isStatic
import cn.tursom.core.isTransient
import cn.tursom.mongodb.annotation.Collection
import cn.tursom.mongodb.annotation.Ignore
import org.bson.BsonValue
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

  fun convertToBson(entity: Any): Document {
    return when (entity) {
      is Map<*, *> -> entity.convert()
      else -> {
        val bson = Document()
        entity.javaClass.declaredFields.filter {
          it.isAccessible = true
          !it.isStatic() && !it.isTransient() && it.getAnnotation(Ignore::class.java) == null
        }.forEach {
          injectValue(bson, it.get(entity) ?: return@forEach, it)
        }
        bson
      }
    }
  }

  fun injectValue(bson: Document, value: Any, field: Field) {
    when (value) {
      is Pair<*, *> -> bson[value.first?.toString() ?: return] = convertToBson(value.second ?: return)
      is Map.Entry<*, *> -> bson[value.key?.toString() ?: return] =
        convertToBson(value.value ?: return)
      else -> bson[fieldName(field)] = value.convert() ?: return
    }
  }

  private fun Iterator<*>.convert(): List<*> {
    val list = ArrayList<Any?>()
    forEach {
      list.add(it.convert() ?: return@forEach)
    }
    return list
  }

  private fun Map<*, *>.convert(): Document {
    val doc = Document()
    forEach { any, u ->
      any ?: return@forEach
      doc[any.toString()] = u.convert() ?: return@forEach
    }
    return doc
  }

  private fun Any?.convert() = when (this) {
    null -> null
    is Enum<*> -> name
    is Boolean -> this
    is Number -> this
    is String -> this
    is Bson -> this
    is BsonValue -> this
    is Map<*, *> -> this.convert()
    is Iterator<*> -> this.convert()
    is Iterable<*> -> this.iterator().convert()
    else -> convertToBson(this)
  }
}