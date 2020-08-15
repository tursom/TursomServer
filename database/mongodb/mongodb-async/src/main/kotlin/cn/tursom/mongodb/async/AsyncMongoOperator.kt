package cn.tursom.mongodb.async

import cn.tursom.mongodb.BsonFactoryImpl
import cn.tursom.mongodb.IndexBuilder
import cn.tursom.mongodb.MongoUtil
import cn.tursom.mongodb.Update
import cn.tursom.mongodb.async.subscriber.*
import cn.tursom.utils.AsyncIterator
import com.mongodb.client.model.*
import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.Document
import org.bson.conversions.Bson
import org.reactivestreams.Publisher
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KProperty1

class AsyncMongoOperator<T : Any>(
  @Suppress("MemberVisibilityCanBePrivate") val collection: MongoCollection<Document>,
  clazz: Class<T>
) : MongoCollection<Document> by collection, BsonFactoryImpl<T>(clazz) {
  constructor(clazz: Class<T>, db: MongoDatabase) : this(db.getCollection(MongoUtil.collectionName(clazz)), clazz)

  suspend fun save(entity: T, options: InsertOneOptions = InsertOneOptions()) {
    insertOne(entity, options)
  }


  suspend fun save(entities: Collection<T>, options: InsertManyOptions = InsertManyOptions()) {
    insertMany(entities, options)
  }

  suspend fun insertOne(entity: T, options: InsertOneOptions = InsertOneOptions()): InsertOneResult? {
    return suspendCoroutine { cont ->
      insertOne(convertToBson(entity), options).subscribe(SuspendInsertOneSubscriber(cont))
    }
  }

  suspend fun insertMany(entities: Collection<T>, options: InsertManyOptions = InsertManyOptions()): List<InsertManyResult> {
    val publisher = collection.insertMany(entities.map { convertToBson(it) }, options)
    return suspendCoroutine { cont ->
      publisher.subscribe(SuspendInsertListSubscriber(cont, entities.size.toLong()))
    }
  }

  suspend fun update(update: Bson, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult? {
    val publisher = collection.updateOne(where, update, options)
    return suspendCoroutine { cont ->
      publisher.subscribe(SuspendInsertOneSubscriber(cont))
    }
  }

  suspend fun update(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult? {
    return update(convertToBson(entity), where, options)
  }

  @Suppress("SpellCheckingInspection")
  suspend fun upsert(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult? {
    return update(entity, where, options.upsert(true))
  }

  @Suppress("SpellCheckingInspection")
  suspend fun upsert(update: Bson, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult? {
    return update(update, where, options.upsert(true))
  }

  suspend fun add(field: KProperty1<T, Number?>, value: Number, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult? {
    return upsert(
      Update { field inc value },
      where, options
    )
  }

  suspend fun inc(field: KProperty1<T, Number?>, where: Bson): UpdateResult? {
    return add(field, 1, where)
  }

  suspend fun getOne(where: Bson? = null): T? {
    val publisher = if (where == null) find() else find(where)
    return suspendCoroutine { cont ->
      publisher.subscribe(SuspendOneSubscriber(this, cont))
    }
  }

  suspend fun list(where: Bson? = null): List<T> {
    val publisher = if (where == null) find() else find(where)
    return suspendCoroutine { cont ->
      publisher.subscribe(SuspendListSubscriber(this, cont))
    }
  }

  fun get(where: Bson? = null, bufSize: Int = 32): AsyncIterator<T> {
    val find = if (where == null) find() else find(where)
    return iterator(find, bufSize)
  }

  fun aggregate(vararg pipeline: Bson, bufSize: Int = 32) = iterator(aggregate(pipeline.asList()), bufSize)

  private fun iterator(publisher: Publisher<Document>, bufSize: Int = 32): AsyncIterator<T> {
    val subscriber = AsyncIteratorSubscriber(this, bufSize)
    publisher.subscribe(subscriber)
    return subscriber
  }

  fun delete(entity: T, options: DeleteOptions = DeleteOptions()) {
    deleteOne(convertToBson(entity), options)
  }

  suspend fun saveIfNotExists(entity: T) {
    val document = convertToBson(entity)
    upsert(document, document)
  }

  suspend fun count(): Long {
    val publisher = countDocuments()
    return suspendCoroutine { cont ->
      publisher.subscribe(SuspendInsertOneSubscriber(cont))
    } ?: 0L
  }

  suspend fun createIndexSuspend(key: Bson, indexOptions: IndexOptions = IndexOptions()): String? {
    return suspendCoroutine { cont ->
      createIndex(key, indexOptions).subscribe(SuspendInsertOneSubscriber(cont))
    }
  }

  suspend fun createIndexSuspend(indexBuilder: IndexBuilder.() -> Unit): String? {
    val builder = IndexBuilder()
    builder.indexBuilder()
    return createIndexSuspend(builder.indexDocument, builder.indexOption)
  }

  override fun toString(): String {
    return "MongoOperator(collection=$collection, clazz=$clazz)"
  }
}