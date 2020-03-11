package cn.tursom.mongodb

import cn.tursom.core.*
import cn.tursom.mongodb.annotation.Ignore
import com.mongodb.client.AggregateIterable
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.DeleteOptions
import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import org.bson.Document
import org.bson.conversions.Bson
import kotlin.reflect.KProperty1

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter", "unused")
open class MongoOperator<T : Any>(
  val collection: MongoCollection<Document>,
   clazz: Class<T>
) : MongoCollection<Document> by collection, BsonFactoryImpl<T>(clazz) {
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

  fun iter(where: Bson? = null): Iterator<T?> {
    val find = find(where)
    return object : Iterator<T?> {
      val iterator = find.iterator()
      override fun hasNext(): Boolean = iterator.hasNext()
      override fun next(): T? = parse(iterator.next())
    }
  }

  override fun find(filter: Bson?): FindIterable<Document> {
    return if (filter != null) {
      collection.find(filter)
    } else {
      collection.find()
    }
  }

  fun get(filter: Bson? = null): Iterable<T> {
    val result = if (filter != null) {
      collection.find(filter)
    } else {
      collection.find()
    }

    return object : Iterable<T> {
      override fun iterator(): Iterator<T> = object : Iterator<T> {
        val iterator = result.iterator()
        override fun hasNext(): Boolean = iterator.hasNext()
        override fun next(): T = Parser.parse(iterator.next(), clazz)!!
      }
    }
  }

  fun aggregate(vararg pipeline: Bson): AggregateIterable<Document> = aggregate(pipeline.asList())

  fun delete(entity: T, options: DeleteOptions = DeleteOptions()) {
    deleteOne(convertToBson(entity), options)
  }

  fun saveIfNotExists(entity: T) {
    val document = convertToBson(entity)
    upsert(document, document)
  }
}

