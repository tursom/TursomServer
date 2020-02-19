package cn.tursom.mongodb

import cn.tursom.core.*
import cn.tursom.mongodb.annotation.Ignore
import com.mongodb.client.FindIterable
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
  val collection: MongoCollection<Document>,
  val clazz: Class<T>
) : MongoCollection<Document> by collection {
  constructor(clazz: Class<T>, database: MongoDatabase) : this(database.getCollection(MongoUtil.collectionName(clazz)), clazz)

  private val fields = clazz.declaredFields.filter {
    it.isAccessible = true
    !it.isStatic() && !it.isTransient() && it.getAnnotation(Ignore::class.java) == null
  }

  fun save(entity: T, options: InsertOneOptions = InsertOneOptions()) {
    collection.insertOne(convertToBson(entity), options)
  }

  fun save(entities: Collection<T>, options: InsertManyOptions = InsertManyOptions()) {
    collection.insertMany(entities.map { convertToBson(it) }, options)
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

  fun getOne(where: Bson? = null): T? {
    val iterator = find(where).iterator()
    if (iterator.hasNext().not()) return null
    return Parser.parse(iterator.next(), clazz)
  }

  fun list(where: Bson? = null): List<T> {
    val find = find(where)
    return find.mapNotNull { Parser.parse(it, clazz) }
  }

  override fun find(filter: Bson?): FindIterable<Document> {
    return if (filter != null) {
      collection.find(filter)
    } else {
      collection.find()
    }
  }

  private fun convertToBson(entity: Any): Document {
    val bson = Document()
    fields.forEach {
      MongoUtil.injectValue(bson, it.get(entity) ?: return@forEach, it)
    }
    return bson
  }

  private fun convertToEntity(bson: Document): T {
    val entity = try {
      clazz.newInstance()
    } catch (e: Exception) {
      Unsafe.unsafe.allocateInstance(clazz)
    }.cast<T>()
    fields.forEach {
      val value = bson[MongoUtil.fieldName(it)] ?: return@forEach

      MongoUtil.injectValue(bson, it.get(entity) ?: return@forEach, it)
    }
    return entity
  }

}