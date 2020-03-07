package cn.tursom.core

import java.lang.reflect.Array
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

object Parser {
  private val simpleDateFormat = ThreadLocalSimpleDateFormat()
  private val Field.actualTypeArguments get() = (genericType as ParameterizedType).actualTypeArguments[0] as Class<*>

  fun <T> parse(yaml: Any, clazz: Class<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return when {
      clazz.isInstance(yaml) -> yaml.cast()
      clazz.isInheritanceFrom(Enum::class.java) -> try {
        val valueOf = clazz.getDeclaredMethod("valueOf", String::class.java)
        valueOf.invoke(null, yaml.toString().toUpperCase()) as T
      } catch (e: Exception) {
        null
      }
      yaml is List<*> && clazz.isArray -> parseArray(yaml, clazz) as T
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
          val instance = try {
            clazz.newInstance()
          } catch (e: Exception) {
            unsafe.allocateInstance(clazz)
          }
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
      } as T
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
      null -> simpleDateFormat.get().parse(this)
      else -> Date(time)
    }
    is Iterable<*> -> null
    is Iterator<*> -> null
    is Map<*, *> -> null
    else -> {
      val str = toString()
      when (val time = str.toLongOrNull()) {
        null -> simpleDateFormat.get().parse(str)
        else -> Date(time)
      }
    }
  }
}