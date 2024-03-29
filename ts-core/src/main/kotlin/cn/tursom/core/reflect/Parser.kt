package cn.tursom.core.reflect

import cn.tursom.core.util.ThreadLocalSimpleDateFormat
import cn.tursom.core.util.Unsafe.unsafe
import cn.tursom.core.util.cast
import cn.tursom.core.util.getClazz
import cn.tursom.core.util.isInheritanceFrom
import java.lang.reflect.Array
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

object Parser {
  private val dateFormat = ThreadLocalSimpleDateFormat()
  private val Field.actualTypeArguments get() = (genericType as ParameterizedType).actualTypeArguments[0] as Class<*>

  fun withDateFormat(format: String, action: Parser.() -> Unit) {
    val defaultFormat = dateFormat.get()
    dateFormat.set(SimpleDateFormat(format))
    try {
      this.action()
    } finally {
      dateFormat.set(defaultFormat)
    }
  }

  fun <T> parse(yaml: Any, clazz: Class<T>): T? {
    return when {
      clazz.isInstance(yaml) -> yaml.cast()
      clazz.isInheritanceFrom(Enum::class.java) -> try {
        val valueOf = clazz.getDeclaredMethod("valueOf", String::class.java)
        valueOf.invoke(null, yaml.toString().uppercase(Locale.getDefault())).cast<T?>()
      } catch (e: Exception) {
        null
      }
      clazz.isInheritanceFrom(Map::class.java) -> {
        val map = try {
          clazz.newInstance()
        } catch (e: Exception) {
          try {
            unsafe.allocateInstance(clazz)
          } catch (e: Exception) {
            HashMap<Any?, Any?>()
          }
        }.cast<MutableMap<Any?, Any?>>()
        if (yaml !is Map<*, *>) return null
        yaml.forEach { (any, u) ->
          map[any] = u
        }
        map.cast()
      }
      yaml is List<*> && clazz.isArray -> parseArray(yaml, clazz).cast()
      else -> when (clazz) {
        Any::class.java -> yaml
        Int::class.java -> yaml.toInt()
        Long::class.java -> yaml.toLong()
        Float::class.java -> yaml.toFloat()
        Double::class.java -> yaml.toDouble()
        Boolean::class.java -> yaml.toBoolean()
        Date::class.java -> yaml.toDate()

        getClazz<Int>() -> yaml.toInt()
        getClazz<Long>() -> yaml.toLong()
        getClazz<Float>() -> yaml.toFloat()
        getClazz<Double>() -> yaml.toDouble()
        getClazz<Boolean>() -> yaml.toBoolean()
        String::class.java -> yaml.toString()

        else -> {
          if (yaml !is Map<*, *>) return null

          val instance = InstantAllocator[clazz]
          val fields = clazz.declaredFields
          fields.forEach {
            if ((it.modifiers and (Modifier.STATIC or Modifier.TRANSIENT)) != 0) return@forEach
            try {
              val parse = parseField(yaml[it.name] ?: return@forEach, it) ?: return@forEach
              it.isAccessible = true
              it.set(instance, parse)
            } catch (e: Exception) {
            }
          }
          instance
        }
      }.cast()
    }
  }

  private fun parseField(yaml: Any, field: Field): Any? {
    val clazz = field.type
    @Suppress("UNCHECKED_CAST")
    return when (yaml) {
      is List<*> -> {
        when {
          clazz.isAssignableFrom(List::class.java) -> {
            val type = field.actualTypeArguments
            if (type == Any::class.java) {
              yaml
            } else {
              val list = try {
                clazz.newInstance() as MutableList<Any>
              } catch (e: Exception) {
                try {
                  unsafe.allocateInstance(clazz) as MutableList<Any>
                } catch (e: Exception) {
                  ArrayList<Any>()
                }
              }
              yaml.forEach {
                list.add(parse(it ?: return@forEach, type) ?: return@forEach)
              }
              list
            }
          }
          clazz.isAssignableFrom(Set::class.java) -> {
            val type = field.actualTypeArguments
            if (type == Any::class.java) {
              yaml
            } else {
              val set: MutableSet<Any> = try {
                clazz.newInstance() as MutableSet<Any>
              } catch (e: Exception) {
                try {
                  unsafe.allocateInstance(clazz) as MutableSet<Any>
                } catch (e: Exception) {
                  HashSet()
                }
              }
              yaml.forEach {
                set.add(parse(it ?: return@forEach, type) ?: return@forEach)
              }
              set
            }
          }
          clazz.isArray -> parseArray(yaml, clazz)
          else -> null
        }
      }
      else -> {
        parse(yaml, clazz)
      }
    }
  }

  private fun <T> parseArray(yaml: List<Any?>, clazz: Class<T>): kotlin.Array<T>? {
    val componentType = clazz.componentType
    val list = ArrayList<Any>()
    yaml.forEach {
      list.add(parse(it ?: return@forEach, componentType) ?: return@forEach)
    }
    val instance = Array.newInstance(componentType, list.size)
    list.forEachIndexed { index, any -> Array.set(instance, index, any) }
    @Suppress("UNCHECKED_CAST")
    return instance as kotlin.Array<T>?
  }

  private fun Any.toInt() = when (this) {
    is Number -> toInt()
    is Boolean -> if (this) 1 else 0
    is String -> toIntOrNull()
    is Iterable<*> -> null
    is Iterator<*> -> null
    is Map<*, *> -> null
    else -> toString().toIntOrNull()
  }

  private fun Any.toLong() = when (this) {
    is Number -> toLong()
    is Boolean -> if (this) 1L else 0L
    is String -> toLongOrNull()
    is Iterable<*> -> null
    is Iterator<*> -> null
    is Map<*, *> -> null
    else -> toString().toLongOrNull()
  }

  private fun Any.toFloat() = when (this) {
    is Number -> toFloat()
    is Boolean -> if (this) 1.0f else 0.0f
    is String -> toFloatOrNull()
    is Iterable<*> -> null
    is Iterator<*> -> null
    is Map<*, *> -> null
    else -> toString().toFloatOrNull()
  }

  private fun Any.toDouble() = when (this) {
    is Number -> toDouble()
    is Boolean -> if (this) 1.0 else 0.0
    is String -> toDoubleOrNull()
    is Iterable<*> -> null
    is Iterator<*> -> null
    is Map<*, *> -> null
    else -> toString().toDoubleOrNull()
  }

  private fun Any.toBoolean(): Boolean? = when (this) {
    is Boolean -> this
    is Number -> 0 != toInt()
    is String -> equals("t", true) || equals("true", true)
    is Iterable<*> -> null
    is Iterator<*> -> null
    is Map<*, *> -> null
    else -> toString().toBoolean()
  }

  private fun Any.toDate(): Date? = when (this) {
    is Number -> Date(toLong())
    is Boolean -> null
    is String -> when (val time = toLongOrNull()) {
      null -> dateFormat.get().parse(this)
      else -> Date(time)
    }
    is Iterable<*> -> null
    is Iterator<*> -> null
    is Map<*, *> -> null
    else -> {
      val str = toString()
      when (val time = str.toLongOrNull()) {
        null -> dateFormat.get().parse(str)
        else -> Date(time)
      }
    }
  }
}