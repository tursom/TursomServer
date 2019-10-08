package cn.tursom.database.mongodb

import com.mongodb.MongoClient

//TODO
class MongoHelper(private val mongoClient: MongoClient) {
	constructor(host: String = "localhost", port: Int = 27017) : this(MongoClient(host, port))
}
