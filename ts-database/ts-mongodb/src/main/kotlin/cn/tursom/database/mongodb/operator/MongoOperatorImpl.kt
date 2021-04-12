package cn.tursom.database.mongodb.operator

import cn.tursom.core.datastruct.AsyncIterator
import cn.tursom.database.mongodb.*
import cn.tursom.database.mongodb.subscriber.*
import cn.tursom.log.Slf4j
import cn.tursom.log.impl.Slf4jImpl
import com.mongodb.client.model.*
import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.Document
import org.bson.conversions.Bson
import org.reactivestreams.Publisher
import java.util.concurrent.Executor
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KProperty1

@Suppress("MemberVisibilityCanBePrivate")
class MongoOperatorImpl<T : Any>(
  @Suppress("MemberVisibilityCanBePrivate") val collection: MongoCollection<Document>,
  clazz: Class<T>,
  private val subscribeExecutor: Executor = MongoUtil.mongoExecutor,
) : MongoOperator<T>, MongoCollection<Document> by collection, BsonFactoryImpl<T>(clazz) {
  companion object : Slf4j by Slf4jImpl()

  constructor(
    clazz: Class<T>,
    db: MongoDatabase,
    subscribeExecutor: Executor = MongoUtil.mongoExecutor,
  ) : this(db.getCollection(MongoUtil.collectionName(clazz)), clazz, subscribeExecutor)

  override suspend fun save(entity: T, options: InsertOneOptions): Boolean {
    return insertOne(entity, options)?.wasAcknowledged() ?: false
  }

  override suspend fun save(entities: Collection<T>, options: InsertManyOptions) {
    insertMany(entities, options)
  }

  override suspend fun saveDocument(document: Document, options: InsertOneOptions): InsertOneResult? {
    return suspendCoroutine { cont ->
      insertOne(document, options).subscribe(SuspendInsertOneSubscriber(cont, subscribeExecutor = subscribeExecutor))
    }
  }

  override suspend fun saveDocument(documents: List<Document>, options: InsertManyOptions): InsertManyResult? {
    val publisher = collection.insertMany(documents, options)
    return suspendCoroutine { cont ->
      publisher.subscribe(
        SuspendInsertOneSubscriber(
          cont,
          documents.size.toLong(),
          subscribeExecutor = subscribeExecutor
        )
      )
    }
  }

  override suspend fun insertOne(entity: T, options: InsertOneOptions): InsertOneResult? {
    return saveDocument(convertToBson(entity))
  }

  override suspend fun insertMany(entities: Collection<T>, options: InsertManyOptions): InsertManyResult? {
    return saveDocument(entities.map { convertToBson(it) }, options)
    //val publisher = collection.insertMany(entities.map { convertToBson(it) }, options)
    //return suspendCoroutine { cont ->
    //    publisher.subscribe(SuspendInsertListSubscriber(cont, entities.size.toLong(), subscribeExecutor = subscribeExecutor))
    //}
  }

  override suspend fun updateMulti(update: Bson, where: Bson, options: UpdateOptions): UpdateResult? {
    val publisher = collection.updateMany(where, update, options)
    return suspendCoroutine { cont ->
      publisher.subscribe(SuspendInsertOneSubscriber(cont, subscribeExecutor = subscribeExecutor))
    }
  }

  override suspend fun update(update: Bson, where: Bson, options: UpdateOptions): UpdateResult? {
    val publisher = collection.updateOne(where, update, options)
    return suspendCoroutine { cont ->
      publisher.subscribe(SuspendInsertOneSubscriber(cont, subscribeExecutor = subscribeExecutor))
    }
  }

  override suspend fun update(entity: T, where: Bson, options: UpdateOptions): UpdateResult? {
    return update(convertToBson(entity), where, options)
  }

  @Suppress("SpellCheckingInspection")
  override suspend fun upsert(entity: T, where: Bson, options: UpdateOptions): UpdateResult? {
    return update(entity, where, options.upsert(true))
  }

  @Suppress("SpellCheckingInspection")
  override suspend fun upsert(update: Bson, where: Bson, options: UpdateOptions): UpdateResult? {
    return update(update, where, options.upsert(true))
  }

  @Suppress("SpellCheckingInspection")
  override suspend fun upsert(
    update: Update.() -> Bson,
    where: Where.() -> Bson,
    options: UpdateOptions,
  ): UpdateResult? {
    return upsert(Update.update(), Where.where(), options)
  }

  override suspend fun add(
    field: KProperty1<T, Number?>,
    value: Number,
    where: Bson,
    options: UpdateOptions,
  ): UpdateResult? {
    return upsert(
      Update { field inc value },
      where, options
    )
  }

  override suspend fun inc(field: KProperty1<T, Number?>, where: Bson): UpdateResult? {
    return add(field, 1, where)
  }

  override suspend fun getOne(where: Bson?): T? {
    val publisher = if (where == null) find() else find(where)
    return suspendCoroutine { cont ->
      publisher.subscribe(SuspendOneSubscriber(this, cont, subscribeExecutor = subscribeExecutor))
    }
  }

  override suspend fun list(where: Bson?, skip: Int, limit: Int): List<T> {
    val publisher = if (where == null) find() else find(where)
    publisher.skip(skip)
    if (limit > 0) {
      publisher.limit(limit)
    }
    return suspendCoroutine { cont ->
      publisher.subscribe(
        SuspendListSubscriber(
          this,
          cont,
          if (limit > 0) limit.toLong() else Long.MAX_VALUE,
          subscribeExecutor = subscribeExecutor
        )
      )
    }
  }

  override suspend fun listDocument(where: Bson?, skip: Int, limit: Int): List<Document> {
    println("skip: $skip, limit: $limit")
    val publisher = if (where == null) find() else find(where)
    publisher.skip(skip)
    if (limit > 0) {
      publisher.limit(limit)
    }
    return suspendCoroutine { cont ->
      publisher.subscribe(
        SuspendListDocumentSubscriber(
          cont,
          if (limit > 0) limit.toLong() else Long.MAX_VALUE,
          subscribeExecutor = subscribeExecutor
        )
      )
    }
  }

  override fun get(where: Bson?, bufSize: Int): AsyncIterator<T> {
    val find = if (where == null) find() else find(where)
    return iterator(find, bufSize)
  }

  override fun getDocument(where: Bson?, bufSize: Int, skip: Int, limit: Int): AsyncIterator<Document> {
    val find = if (where == null) find() else find(where)
    find.skip(skip)
    if (limit > 0) {
      find.limit(limit)
    }
    return documentIterator(find, bufSize)
  }

  override fun aggregate(vararg pipeline: Bson, bufSize: Int): AsyncIterator<T> =
    iterator(aggregate(pipeline.asList()), bufSize)

  private fun iterator(publisher: Publisher<Document>, bufSize: Int = 32): AsyncIterator<T> {
    val subscriber = AsyncIteratorSubscriber(
      this,
      bufSize,
      subscribeExecutor = subscribeExecutor
    )
    publisher.subscribe(subscriber)
    return subscriber
  }

  private fun documentIterator(publisher: Publisher<Document>, bufSize: Int = 32): AsyncIterator<Document> {
    val subscriber = AsyncDocumentIteratorSubscriber(bufSize, subscribeExecutor = subscribeExecutor)
    publisher.subscribe(subscriber)
    return subscriber
  }

  override fun delete(entity: T, options: DeleteOptions) {
    deleteOne(convertToBson(entity), options)
  }

  override suspend fun saveIfNotExists(entity: T) {
    val document = convertToBson(entity)
    upsert(document, document)
  }

  override suspend fun count(): Long {
    val publisher = countDocuments()
    return suspendCoroutine { cont ->
      publisher.subscribe(SuspendInsertOneSubscriber(cont, subscribeExecutor = subscribeExecutor))
    } ?: 0L
  }

  override suspend fun count(where: Bson): Long {
    val publisher = countDocuments(where)
    return suspendCoroutine { cont ->
      publisher.subscribe(SuspendInsertOneSubscriber(cont, subscribeExecutor = subscribeExecutor))
    } ?: 0L
  }

  override suspend fun createIndexSuspend(key: Bson, indexOptions: IndexOptions): String? {
    return suspendCoroutine { cont ->
      createIndex(key, indexOptions).subscribe(SuspendInsertOneSubscriber(cont, subscribeExecutor = subscribeExecutor))
    }
  }

  override suspend fun createIndexSuspend(indexBuilder: IndexBuilder.() -> Unit): String? {
    val builder = IndexBuilder()
    builder.indexBuilder()
    return createIndexSuspend(builder.indexDocument, builder.indexOption)
  }

  override fun toString(): String {
    return "MongoOperator(collection=$collection, clazz=$clazz)"
  }
}