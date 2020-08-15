package cn.tursom.mongodb.async

import com.mongodb.reactivestreams.client.MongoClient

fun MongoClient.getMongoTemplate(db: String) = AsyncMongoTemplate(this, db)