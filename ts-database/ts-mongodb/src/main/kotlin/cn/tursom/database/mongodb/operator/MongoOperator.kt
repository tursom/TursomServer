package cn.tursom.database.mongodb.operator

import cn.tursom.core.datastruct.AsyncIterator
import cn.tursom.database.mongodb.BsonFactory
import cn.tursom.database.mongodb.IndexBuilder
import cn.tursom.database.mongodb.Update
import cn.tursom.database.mongodb.Where
import com.mongodb.client.model.*
import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import org.bson.Document
import org.bson.conversions.Bson
import java.io.Closeable
import kotlin.reflect.KProperty1

interface MongoOperator<T : Any> : BsonFactory<T>, Closeable {
  suspend fun save(entity: T, options: InsertOneOptions = InsertOneOptions()): Boolean {
    return insertOne(entity, options)?.wasAcknowledged() ?: false
  }

  suspend fun save(entities: Collection<T>, options: InsertManyOptions = InsertManyOptions()) {
    insertMany(entities, options)
  }

  suspend fun saveDocument(document: Document, options: InsertOneOptions = InsertOneOptions()): InsertOneResult?

  suspend fun saveDocument(
    documents: List<Document>,
    options: InsertManyOptions = InsertManyOptions()
  ): InsertManyResult?

  suspend fun insertOne(entity: T, options: InsertOneOptions = InsertOneOptions()): InsertOneResult? {
    return saveDocument(convertToBson(entity))
  }

  suspend fun insertMany(entities: Collection<T>, options: InsertManyOptions = InsertManyOptions()): InsertManyResult? {
    return saveDocument(entities.map { convertToBson(it) }, options)
  }

  suspend fun updateMulti(update: Bson, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult?

  suspend fun update(update: Bson, where: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult?

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

  @Suppress("SpellCheckingInspection")
  suspend fun upsert(
    update: Update.() -> Bson,
    where: Where.() -> Bson,
    options: UpdateOptions = UpdateOptions(),
  ): UpdateResult? {
    return upsert(Update.update(), Where.where(), options)
  }

  suspend fun add(
    field: KProperty1<T, Number?>,
    value: Number,
    where: Bson,
    options: UpdateOptions = UpdateOptions(),
  ): UpdateResult? {
    return upsert(
      Update { field inc value },
      where, options
    )
  }

  suspend fun inc(field: KProperty1<T, Number?>, where: Bson): UpdateResult? {
    return add(field, 1, where)
  }

  suspend fun getOne(where: Bson? = null): T?

  suspend fun list(where: Bson? = null, skip: Int = 0, limit: Int = 0): List<T>

  suspend fun listDocument(where: Bson? = null, skip: Int = 0, limit: Int = 0): List<Document>

  fun get(where: Bson? = null, bufSize: Int = 32): AsyncIterator<T>

  fun getDocument(where: Bson? = null, bufSize: Int = 32, skip: Int = 0, limit: Int = 0): AsyncIterator<Document>

  fun aggregate(vararg pipeline: Bson, bufSize: Int = 32): AsyncIterator<T>

  fun delete(entity: T, options: DeleteOptions = DeleteOptions())

  suspend fun saveIfNotExists(entity: T) {
    val document = convertToBson(entity)
    upsert(document, document)
  }

  suspend fun count(): Long
  suspend fun count(where: Bson): Long

  suspend fun createIndexSuspend(key: Bson, indexOptions: IndexOptions = IndexOptions()): String?

  suspend fun createIndexSuspend(indexBuilder: IndexBuilder.() -> Unit): String? {
    val builder = IndexBuilder()
    builder.indexBuilder()
    return createIndexSuspend(builder.indexDocument, builder.indexOption)
  }

  override fun close() {}
}