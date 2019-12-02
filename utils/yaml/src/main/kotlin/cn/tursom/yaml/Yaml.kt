package cn.tursom.yaml

import org.yaml.snakeyaml.Yaml
import sun.misc.Unsafe
import java.lang.reflect.Array
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType


@Suppress("MemberVisibilityCanBePrivate", "unused")
object Yaml {
  private val yaml = Yaml()
  private val unsafe = run {
    val field = Unsafe::class.java.getDeclaredField("theUnsafe")
    field.isAccessible = true
    field.get(null) as Unsafe
  }

  private inline fun <reified T> getClazz() = T::class.java
  private val Field.actualTypeArguments get() = (genericType as ParameterizedType).actualTypeArguments[0] as Class<*>

  fun toYaml(obj: Any): String {
    val stringBuilder = StringBuilder()
    toYaml(obj, stringBuilder, "")
    return stringBuilder.toString()
  }

  private fun toYaml(obj: Any, stringBuilder: StringBuilder, indentation: String) {
    when (obj) {
      is Byte -> stringBuilder.append("$obj")
      is Char -> stringBuilder.append("$obj")
      is Short -> stringBuilder.append("$obj")
      is Int -> stringBuilder.append("$obj")
      is Long -> stringBuilder.append("$obj")
      is Float -> stringBuilder.append("$obj")
      is Double -> stringBuilder.append("$obj")
      is String -> stringBuilder.append("$obj")
      is Collection<*> -> obj.forEach {
        it ?: return@forEach
        stringBuilder.append("$indentation- ")
        toYaml(it, stringBuilder, "$indentation  ")
        if (!stringBuilder.endsWith('\n')) {
          stringBuilder.append("\n")
        }
      }
      is Map<*, *> -> obj.forEach { (any, u) ->
        stringBuilder.append("$indentation${any ?: return@forEach}: ")
        toYaml(u ?: return@forEach, stringBuilder, "$indentation  ")
        if (!stringBuilder.endsWith('\n')) {
          stringBuilder.append("\n")
        }
      }
      else -> {
        obj.javaClass.declaredFields.forEach {
          if ((it.modifiers and (Modifier.STATIC or Modifier.TRANSIENT)) != 0) return@forEach
          it.isAccessible = true
          val value = it.get(obj)
          when (it.type) {
            Byte::class.java -> stringBuilder.append("$indentation${it.name}: $value\n")
            Char::class.java -> stringBuilder.append("$indentation${it.name}: $value\n")
            Short::class.java -> stringBuilder.append("$indentation${it.name}: $value\n")
            Int::class.java -> stringBuilder.append("$indentation${it.name}: $value\n")
            Long::class.java -> stringBuilder.append("$indentation${it.name}: $value\n")
            Float::class.java -> stringBuilder.append("$indentation${it.name}: $value\n")
            Double::class.java -> stringBuilder.append("$indentation${it.name}: $value\n")

            getClazz<Byte>() -> stringBuilder.append("$indentation${it.name}: $value\n")
            getClazz<Char>() -> stringBuilder.append("$indentation${it.name}: $value\n")
            getClazz<Short>() -> stringBuilder.append("$indentation${it.name}: $value\n")
            getClazz<Int>() -> stringBuilder.append("$indentation${it.name}: $value\n")
            getClazz<Long>() -> stringBuilder.append("$indentation${it.name}: $value\n")
            getClazz<Float>() -> stringBuilder.append("$indentation${it.name}: $value\n")
            getClazz<Double>() -> stringBuilder.append("$indentation${it.name}: $value\n")
            String::class.java -> stringBuilder.append("$indentation${it.name}: $value\n")

            else -> {
              stringBuilder.append("$indentation${it.name}:\n")
              toYaml(value, stringBuilder, "$indentation  ")
            }
          }
        }
      }
    }

  }

  inline fun <reified T> parse(yaml: Any) = parse(yaml, T::class.java)
  inline fun <reified T> parse(yaml: String) = parse(yaml, T::class.java)
  inline fun <reified T> parseResource(path: String) = parseResource(path, T::class.java)
  inline fun <reified T> parseResource(classLoader: ClassLoader, path: String) = parseResource(classLoader, path, T::class.java)

  fun <T> parseResource(path: String, clazz: Class<T>) = parseResource(this.javaClass.classLoader, path, clazz)
  fun <T> parseResource(classLoader: ClassLoader, path: String, clazz: Class<T>) = parse(yaml.load<Any>(classLoader.getResourceAsStream(path)), clazz)
  fun <T> parse(yaml: String, clazz: Class<T>) = parse(this.yaml.load<Any>(yaml), clazz)

  fun <T> parse(yaml: Any, clazz: Class<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return if (yaml is List<*> && clazz.isArray) {
      parseArray(yaml, clazz) as T
    } else {
      return when (clazz) {
        Int::class.java -> yaml.toInt()
        Long::class.java -> yaml.toLong()
        Float::class.java -> yaml.toFloat()
        Double::class.java -> yaml.toDouble()
        Boolean::class.java -> yaml.toBoolean()

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
    return if (yaml is List<*>) {
      when {
        clazz.isAssignableFrom(ArrayList::class.java) -> {
          val type = field.actualTypeArguments
          val list = ArrayList<Any>()
          yaml.forEach {
            list.add(parse(it ?: return@forEach, type) ?: return@forEach)
          }
          list
        }
        MutableList::class.java.isAssignableFrom(clazz) -> {
          val type = field.actualTypeArguments
          val instance = clazz.newInstance() as MutableList<Any>
          yaml.forEach {
            instance.add(parse(it ?: return@forEach, type) ?: return@forEach)
          }
          instance
        }
        clazz.isArray -> parseArray(yaml, clazz)
        else -> null
      }
    } else {
      parse(yaml, clazz)
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
    is Byte -> toInt()
    is Char -> toInt()
    is Short -> toInt()
    is Int -> this
    is Long -> toInt()
    is Float -> toInt()
    is Double -> toInt()
    is Boolean -> if (this) 1 else 0
    is String -> toIntOrNull()
    is Collection<*> -> null
    is Map<*, *> -> null
    else -> toString().toIntOrNull()
  }

  private fun Any.toLong() = when (this) {
    is Byte -> toLong()
    is Char -> toLong()
    is Short -> toLong()
    is Int -> toLong()
    is Long -> this
    is Float -> toLong()
    is Double -> toLong()
    is Boolean -> if (this) 1 else 0
    is String -> toLongOrNull()
    is Collection<*> -> null
    is Map<*, *> -> null
    else -> toString().toLongOrNull()
  }

  private fun Any.toFloat() = when (this) {
    is Byte -> toFloat()
    is Char -> toFloat()
    is Short -> toFloat()
    is Int -> toFloat()
    is Long -> toFloat()
    is Float -> this
    is Double -> toFloat()
    is Boolean -> if (this) 1.0f else 0.0f
    is String -> toFloatOrNull()
    is Collection<*> -> null
    is Map<*, *> -> null
    else -> toString().toFloatOrNull()
  }

  private fun Any.toDouble() = when (this) {
    is Byte -> toDouble()
    is Char -> toDouble()
    is Short -> toDouble()
    is Int -> toDouble()
    is Long -> toDouble()
    is Float -> toDouble()
    is Double -> this
    is Boolean -> if (this) 1.0 else 0.0
    is String -> toDoubleOrNull()
    is Collection<*> -> null
    is Map<*, *> -> null
    else -> toString().toDoubleOrNull()
  }

  private fun Any.toBoolean(): Boolean? = when (this) {
    is Boolean -> this
    is Byte -> 0 != this
    is Char -> 0 != this
    is Short -> 0 != this
    is Int -> 0 != this
    is Long -> 0 != this
    is Float -> 0 != this
    is Double -> 0 != this
    is String -> toBoolean()
    is Collection<*> -> null
    is Map<*, *> -> null
    else -> toString().toBoolean()
  }
}