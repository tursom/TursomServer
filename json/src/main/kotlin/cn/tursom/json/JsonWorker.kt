package cn.tursom.json

interface JsonWorker {
  fun toJson(obj: Any): String?
  fun <T> fromJson(json: String, clazz: Class<T>): T?
}