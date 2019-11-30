package cn.tursom.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson

class JsonWorkerImpl : JsonWorker {
  private val gson = try {
    Gson()
  } catch (e: Throwable) {
    null
  }

  private val jackson = try {
    ObjectMapper()
  } catch (e: Throwable) {
    null
  }

  override fun toJson(obj: Any): String? = gson?.toJson(obj) ?: jackson?.writeValueAsString(obj)

  override fun <T> fromJson(json: String, clazz: Class<T>): T? =
    gson?.fromJson(json, clazz) ?: jackson?.readValue(json, clazz)

  override fun toString(): String {
    return "JsonWorkerImpl(gson=$gson, jackson=$jackson)"
  }
}