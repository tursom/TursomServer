package cn.tursom.database.mongodb.spring

import cn.tursom.core.isStatic
import cn.tursom.core.isTransient
import cn.tursom.core.uncheckedCast
import org.bson.BsonValue
import org.bson.Document
import org.bson.conversions.Bson
import java.lang.reflect.Field

object MongoUtil {
  fun collectionName(clazz: Class<*>) =
    clazz.getAnnotation(org.springframework.data.mongodb.core.mapping.Document::class.java)?.let {
      when {
        it.value.isNotBlank() -> it.value
        it.collection.isNotBlank() -> it.collection
        else -> null
      }
    } ?: clazz.simpleName.toCharArray().let {
      it[0] = it[0].lowercaseChar()
      it
    }.concatToString()

  fun convertToBson(entity: Any): Document {
    return when (entity) {
      is Document -> entity
      is Map<*, *> -> entity.convert()
      else -> {
        val bson = Document()
        entity.javaClass.declaredFields.filter {
          it.isAccessible = true
          !it.isStatic()
              && !it.isTransient()
              //&& it.getAnnotation(Ignore::class.java) == null
              && (it.type != Lazy::class.java || it.get(entity).uncheckedCast<Lazy<*>>().isInitialized())
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
      else -> bson[MongoName.mongoName(field)] = value.convert() ?: return
    }
  }

  fun <T> fromBson(document: Document, bsonFactory: BsonFactory<T>) = bsonFactory.parse(document)
  inline fun <reified T> fromBson(document: Document) = BsonFactory[T::class.java].parse(document)

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
    is Boolean, is Number, is String, is Bson, is BsonValue -> this
    is Map<*, *> -> this.convert()
    is Iterator<*> -> this.convert()
    is Iterable<*> -> this.iterator().convert()
    else -> convertToBson(this)
  }
}