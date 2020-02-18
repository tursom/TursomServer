package cn.tursom.mongodb.impl

import cn.tursom.mongodb.MongoConnection
import cn.tursom.mongodb.MongoTemplate
import com.mongodb.MongoClient

class MongoConnectionImpl(
  db: String,
  val host: String = "127.0.0.1",
  val port: Int = 27017
) : MongoConnection {
  override val client: MongoClient = MongoClient(host, port)

  val db = client.getDatabase(db)

  override fun <T> createTemplate(clazz: Class<T>): MongoTemplate<T> {
    db.createCollection(MongoConnection.collectionName(clazz))
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}