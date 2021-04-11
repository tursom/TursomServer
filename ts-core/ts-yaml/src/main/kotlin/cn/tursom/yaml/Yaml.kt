package cn.tursom.yaml

import cn.tursom.core.Parser
import cn.tursom.core.ThreadLocalSimpleDateFormat
import org.yaml.snakeyaml.Yaml
import java.lang.reflect.Modifier
import java.util.*


@Suppress("MemberVisibilityCanBePrivate", "unused")
object Yaml {
  private val simpleDateFormat = ThreadLocalSimpleDateFormat()
  private val yaml = Yaml()

  fun toYaml(obj: Any): String {
    val stringBuilder = StringBuilder()
    toYaml(obj, stringBuilder, "")
    return stringBuilder.toString()
  }

  private fun toYaml(obj: Any, stringBuilder: StringBuilder, indentation: String, inCollection: Boolean = false) {
    when (obj) {
      is Byte -> stringBuilder.append(obj)
      is Char -> stringBuilder.append(obj)
      is Short -> stringBuilder.append(obj)
      is Int -> stringBuilder.append(obj)
      is Long -> stringBuilder.append(obj)
      is Float -> stringBuilder.append(obj)
      is Double -> stringBuilder.append(obj)
      is Date -> stringBuilder.append(simpleDateFormat.get().format(obj))
      is String -> when {
        obj.contains('\n') -> {
          stringBuilder.append("|${if (obj.endsWith('\n')) '+' else '-'}")
          obj.split('\n').forEach {
            stringBuilder.append("$indentation$it")
          }
        }
        obj.startsWith('|') -> stringBuilder.append("\"$obj\"")
        else -> {
          stringBuilder.append(obj)
        }
      }
      is Map<*, *> -> {
        var first = true
        obj.forEach { (any, u) ->
          if (inCollection && first) {
            stringBuilder.append("${any ?: return@forEach}: ")
            first = false
          } else {
            stringBuilder.append("$indentation${any ?: return@forEach}: ")
          }
          toYaml(u ?: return@forEach, stringBuilder, "$indentation  ")
          if (!stringBuilder.endsWith('\n')) {
            stringBuilder.append("\n")
          }
        }
      }
      is Collection<*> -> if (obj.isEmpty()) {
        stringBuilder.append("[]")
      } else {
        var appended = 0
        obj.forEach {
          it ?: return@forEach
          stringBuilder.append("${if (appended == 0) "\n" else ""}$indentation- ")
          appended++
          toYaml(it, stringBuilder, "$indentation  ", true)
          if (!stringBuilder.endsWith('\n')) {
            stringBuilder.append("\n")
          }
        }
      }
      else -> {
        var first = true
        fun getIndentation() = if (inCollection && first) {
          first = false
          ""
        } else {
          indentation
        }
        obj.javaClass.declaredFields.forEach {
          if ((it.modifiers and (Modifier.STATIC or Modifier.TRANSIENT)) != 0) return@forEach
          it.isAccessible = true
          val value = it.get(obj)
          stringBuilder.append("${getIndentation()}${it.name}: ")
          toYaml(value, stringBuilder, "${getIndentation()}  ")
          if (!stringBuilder.endsWith('\n')) {
            stringBuilder.append("\n")
          }
        }
      }
    }

  }

  inline fun <reified T> parse(yaml: Any) = Parser.parse(yaml, T::class.java)
  inline fun <reified T> parse(yaml: String) = parse(yaml, T::class.java)
  inline fun <reified T> parseResource(path: String) = parseResource(path, T::class.java)
  inline fun <reified T> parseResource(classLoader: ClassLoader, path: String) =
    parseResource(classLoader, path, T::class.java)

  fun <T> parseResource(path: String, clazz: Class<T>) = parseResource(this.javaClass.classLoader, path, clazz)
  fun <T> parseResource(classLoader: ClassLoader, path: String, clazz: Class<T>) =
    Parser.parse(yaml.load(classLoader.getResourceAsStream(path)), clazz)

  fun <T> parse(yaml: String, clazz: Class<T>) = Parser.parse(this.yaml.load(yaml), clazz)
}