package cn.tursom.mongodb

interface MongoTemplate<T> {
  fun save(entity: T)
}