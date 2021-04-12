package cn.tursom.database.mongodb

import com.mongodb.reactivestreams.client.MongoClient

fun MongoClient.getMongoTemplate(db: String) = MongoTemplate(this, db)