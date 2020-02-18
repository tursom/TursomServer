package cn.tursom.mongodb

import cn.tursom.core.cast
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.UpdateOptions
import org.bson.conversions.Bson
import java.util.concurrent.ConcurrentHashMap

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate", "unused")
class MongoTemplate(
  db: String,
  val host: String = "127.0.0.1",
  val port: Int = 27017
) {
  val client = MongoClient(host, port)

  val db: MongoDatabase = client.getDatabase(db)

  private val operatorMap = ConcurrentHashMap<Class<*>, MongoOperator<*>>()

  fun <T : Any> save(entity: T, options: InsertOneOptions = InsertOneOptions()) {
    getCollection(entity.javaClass).save(entity, options)
  }

  inline fun <reified T : Any> save(entities: Collection<T>, options: InsertManyOptions = InsertManyOptions()) {
    getCollection<T>().save(entities, options)
  }

  fun <T : Any> update(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()) {
    getCollection(entity.javaClass).update(entity, where, options)
  }

  @Suppress("SpellCheckingInspection")
  fun <T : Any> upsert(entity: T, where: Bson, options: UpdateOptions = UpdateOptions()) {
    getCollection(entity.javaClass).upsert(entity, where, options)
  }

  inline fun <reified T : Any> getCollection(): MongoOperator<T> = getCollection(T::class.java)

  fun <T : Any> getCollection(clazz: Class<T>): MongoOperator<T> {
    return operatorMap[clazz]?.cast() ?: run {
      val operator = MongoOperator(clazz, db)
      operatorMap[clazz] = operator
      operator
    }
  }
}

