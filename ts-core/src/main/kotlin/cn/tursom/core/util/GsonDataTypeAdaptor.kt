package cn.tursom.core.util

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class GsonDataTypeAdaptor internal constructor(private val gson: Gson) : TypeAdapter<Map<String, Any>?>() {
  override fun write(out: JsonWriter, value: Map<String, Any>?) {
    if (value == null) {
      out.nullValue()
      return
    }
    out.beginObject()
    value.forEach { (t, u) ->
      out.name(t)
      System.err.println(u)
      gson.getAdapter(Any::class.java).write(out, u)
    }
    out.endObject()
  }

  override fun read(`in`: JsonReader): Map<String, Any> = readInternal(`in`).cast()

  private fun readInternal(`in`: JsonReader): Any? {
    return when (`in`.peek()) {
      JsonToken.BEGIN_ARRAY -> {
        val list: MutableList<Any?> = ArrayList()
        `in`.beginArray()
        while (`in`.hasNext()) {
          list.add(readInternal(`in`))
        }
        `in`.endArray()
        list
      }
      JsonToken.BEGIN_OBJECT -> {
        val map: MutableMap<String, Any?> = LinkedTreeMap()
        `in`.beginObject()
        while (`in`.hasNext()) {
          map[`in`.nextName()] = readInternal(`in`)
        }
        `in`.endObject()
        map
      }
      JsonToken.STRING -> `in`.nextString()
      JsonToken.NUMBER -> {
        //将其作为一个字符串读取出来
        val numberStr: String = `in`.nextString()
        //返回的numberStr不会为null
        if (numberStr.contains(".") || numberStr.contains("e")
          || numberStr.contains("E")
        ) {
          numberStr.toDouble()
        } else numberStr.toLong()
      }
      JsonToken.BOOLEAN -> `in`.nextBoolean()
      JsonToken.NULL -> {
        `in`.nextNull()
        null
      }
      else -> throw IllegalStateException()
    }
  }

  companion object {
    val FACTORY: TypeAdapterFactory = object : TypeAdapterFactory {
      override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        return if (type.rawType == Map::class.java) {
          GsonDataTypeAdaptor(gson).cast()
        } else {
          null
        }
      }
    }
  }
}

