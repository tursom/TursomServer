package cn.tursom.mongodb

import cn.tursom.core.isTransient
import cn.tursom.mongodb.annotation.Ignore
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import org.bson.Document
import org.bson.conversions.Bson
import kotlin.reflect.KProperty1

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter", "unused")
class MongoOperator<T : Any>(
  val collection: MongoCollection<T>,
  val clazz: Class<T>
) {
  constructor(clazz: Class<T>, database: MongoDatabase) : this(database.getCollection(MongoUtil.collectionName(clazz), clazz), clazz)

  private val fields = clazz.declaredFields.filter {
    it.isAccessible = true
    !it.isTransient() && it.getAnnotation(Ignore::class.java) == null
  }

  fun save(entity: T, options: InsertOneOptions = InsertOneOptions()) {
    collection.insertOne(entity, options)
  }

  fun save(entities: Collection<T>, options: InsertManyOptions = InsertManyOptions()) {
    collection.insertMany(when (entities) {
      is List<T> -> entities
      else -> entities.toList()
    }, options)
  }

  fun update(update: Bson, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult {
    return collection.updateOne(where, update, options)
  }

  fun update(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult {
    return update(convertToBson(entity), where, options)
  }

  @Suppress("SpellCheckingInspection")
  fun upsert(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult {
    return update(entity, where, options.upsert(true))
  }

  @Suppress("SpellCheckingInspection")
  fun upsert(update: Bson, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult {
    return update(update, where, options.upsert(true))
  }

  fun add(field: KProperty1<T, Number?>, value: Number, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult {
    return upsert(
      Update { field inc value },
      where, options
    )
  }

  fun inc(field: KProperty1<T, Number?>, where: Bson): UpdateResult {
    return add(field, 1, where)
  }

  private fun convertToBson(entity: Any): Bson {
    val bson = Document()
    fields.forEach {
      val value = it.get(entity) ?: return@forEach
      bson[MongoUtil.fieldName(it)] = value
    }
    return bson
  }
}