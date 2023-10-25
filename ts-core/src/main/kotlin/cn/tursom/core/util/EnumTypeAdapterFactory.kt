package cn.tursom.core.util

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

object EnumTypeAdapterFactory : TypeAdapterFactory {
  @Retention(AnnotationRetention.RUNTIME)
  @Target(AnnotationTarget.FIELD)
  annotation class EnumValue

  override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
    if (!type.rawType.isEnum) {
      return null
    }

    val valueOf = type.rawType.getDeclaredMethod("valueOf", String::class.java)

    val enumValueMap = type.rawType.enumConstants.asList().associateWith {
      val enum: T = it.cast()
      enum.javaClass.getField(it.toString()).getAnnotation(SerializedName::class.java)?.let { serializedName ->
        return@associateWith serializedName.value
      }

      val field = enum.javaClass.declaredFields.firstOrNull { it2 ->
        it2.getAnnotation(EnumValue::class.java) != null
      }
      if (field != null) {
        field.isAccessible = true
        val value: Any = field.get(enum)
        value
      } else {
        enum
      }
    }

    return object : TypeAdapter<T>() {
      override fun write(out: JsonWriter, value: T?) {
        if (value == null) {
          out.nullValue()
        } else {
          when (val enumValue = enumValueMap[value]) {
            null -> out.nullValue()
            is Boolean -> out.value(enumValue)
            is Char -> out.value(enumValue.toString())
            is Byte -> out.value(enumValue)
            is Short -> out.value(enumValue)
            is Int -> out.value(enumValue)
            is Long -> out.value(enumValue)
            is Float -> out.value(enumValue)
            is Double -> out.value(enumValue)
            is Enum<*> -> out.value(enumValue.toString())
            else -> out.jsonValue(Utils.gson.toJson(enumValue))
          }
        }
      }

      override fun read(reader: JsonReader): T? {
        if (reader.peek() == JsonToken.NULL) {
          reader.nextNull()
          return null
        } else {
          val source = reader.nextString()
          enumValueMap.forEach { (value, type) ->
            if (type.toString() == source) {
              return value.cast()
            }
          }
          return valueOf(null, source).cast()
        }
      }
    }
  }
}