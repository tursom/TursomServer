package cn.tursom.mongodb.async

import cn.tursom.core.cast
import com.mongodb.MongoClientSettings
import com.mongodb.ServerAddress
import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.bson.conversions.Bson
import java.util.concurrent.ConcurrentHashMap

class AsyncMongoTemplate(
  val mongoClient: MongoClient,
  db: String
) {
  constructor(db: String, vararg servers: ServerAddress) : this(MongoClientSettings.builder().let {
    it.applyToClusterSettings { builder ->
      builder.hosts(servers.asList())
    }
    MongoClients.create(it.build())
  }, db)

  constructor(host: String, port: Int, db: String) : this(db, ServerAddress(host, port))

  val db = mongoClient.getDatabase(db)
  private val operatorMap = ConcurrentHashMap<Class<*>, AsyncMongoOperator<*>>()


  suspend fun <T : Any> save(entity: T, options: InsertOneOptions = InsertOneOptions()) {
    getCollection(entity.javaClass).save(entity, options)
  }

  suspend inline fun <reified T : Any> save(entities: Collection<T>, options: InsertManyOptions = InsertManyOptions()) {
    getCollection<T>().save(entities, options)
  }

  suspend fun <T : Any> update(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()) {
    getCollection(entity.javaClass).update(entity, where, options)
  }

  @Suppress("SpellCheckingInspection")
  suspend fun <T : Any> upsert(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()) {
    getCollection(entity.javaClass).upsert(entity, where, options)
  }

  inline fun <reified T : Any> getCollection(): AsyncMongoOperator<T> = getCollection(T::class.java)

  fun <T : Any> getCollection(clazz: Class<T>): AsyncMongoOperator<T> {
    return operatorMap[clazz]?.cast() ?: run {
      val operator = AsyncMongoOperator(clazz, db)
      operatorMap[clazz] = operator
      operator
    }
  }
}