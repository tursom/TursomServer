package cn.tursom.database.mongodb

import cn.tursom.core.util.uncheckedCast
import cn.tursom.database.mongodb.operator.MongoOperator
import cn.tursom.database.mongodb.operator.MongoOperatorImpl
import com.mongodb.MongoClientSettings
import com.mongodb.ServerAddress
import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.conversions.Bson
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor

class MongoTemplate(
  private val db: MongoDatabase,
  val subscribeExecutor: Executor = MongoUtil.mongoExecutor,
) : MongoDatabase by db {
  constructor(
    mongoClient: MongoClient,
    db: String,
    subscribeExecutor: Executor = MongoUtil.mongoExecutor,
  ) : this(mongoClient.getDatabase(db), subscribeExecutor)

  constructor(
    db: String,
    mongoClientSettingsBuilder: MongoClientSettings.Builder,
    subscribeExecutor: Executor = MongoUtil.mongoExecutor,
  ) : this(MongoClients.create(mongoClientSettingsBuilder.build()), db, subscribeExecutor)

  constructor(
    db: String,
    vararg servers: ServerAddress,
    subscribeExecutor: Executor = MongoUtil.mongoExecutor,
  ) : this(
    db,
    MongoClientSettings.builder().also {
      it.applyToClusterSettings { builder ->
        builder.hosts(servers.asList())
      }
    },
    subscribeExecutor
  )

  constructor(host: String, port: Int, db: String) : this(db, ServerAddress(host, port))

  private val operatorMap = ConcurrentHashMap<Class<*>, MongoOperatorImpl<*>>()

  suspend fun <T : Any> save(entity: T, options: InsertOneOptions = InsertOneOptions()) =
    getCollection(entity.javaClass).save(entity, options)

  suspend inline fun <reified T : Any> save(entities: Collection<T>, options: InsertManyOptions = InsertManyOptions()) =
    getCollection<T>().save(entities, options)

  suspend fun <T : Any> update(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()) =
    getCollection(entity.javaClass).update(entity, where, options)

  @Suppress("SpellCheckingInspection")
  suspend fun <T : Any> upsert(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()) =
    getCollection(entity.javaClass).upsert(entity, where, options)

  inline fun <reified T : Any> getCollection(): MongoOperator<T> = getCollection(T::class.java)

  fun <T : Any> getCollection(clazz: Class<T>): MongoOperator<T> {
    return operatorMap[clazz]?.uncheckedCast() ?: run {
      val operator = MongoOperatorImpl(clazz, db, subscribeExecutor)
      operatorMap[clazz] = operator
      operator
    }
  }

  inline fun <reified T : Any> getOperator(collection: String) = getOperator(collection, T::class.java)
  fun <T : Any> getOperator(collection: String, clazz: Class<T>): MongoOperator<T> {
    return MongoOperatorImpl(getCollection(collection), clazz, subscribeExecutor)
  }

  override fun toString(): String {
    return "MongoTemplate(db=$db)"
  }

  //override fun close() {
  //    //mongoClient.close()
  //}
}