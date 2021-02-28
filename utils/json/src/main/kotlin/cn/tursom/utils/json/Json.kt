package cn.tursom.utils.json

import com.sun.org.apache.xalan.internal.lib.ExsltMath.power
import java.lang.RuntimeException

object Json {
  class JsonFormatException(message: String? = null) : RuntimeException(message) {
    internal constructor(content: JsonParseContent) : this("${content.json}[${content.index}]")
  }

  fun parse(json: String): Any? {
    val content = JsonParseContent(json)
    val parse = parse(content)
    jumpWhitespace(content)
    if (content.index != json.length) throw JsonFormatException("$json[${json[content.index]}] remain characters")
    return parse
  }

  internal data class JsonParseContent(val json: String, var index: Int = 0)

  private fun parse(content: JsonParseContent): Any? {
    jumpWhitespace(content)
    return when (content.json[content.index]) {
      '{' -> parseObj(content)
      '[' -> parseArray(content)
      '"' -> parseString(content)
      '+', '-', in '0'..'9' -> parseNumber(content)
      't', 'f' -> parseBoolean(content)
      'n' -> parseNull(content)
      else -> throw JsonFormatException(content)
    }
  }

  private fun parseNull(content: JsonParseContent) = if (content.json.startsWith("null", content.index)) {
    content.index += 4
    null
  } else throw JsonFormatException(content)

  @Suppress("ControlFlowWithEmptyBody")
  private fun parseBoolean(content: JsonParseContent) = when {
    content.json.startsWith("true", content.index) -> {
      content.index += 4
      true
    }
    content.json.startsWith("false", content.index) -> {
      content.index += 5
      false
    }
    else -> throw JsonFormatException(content)
  }

  private fun jumpWhitespaceLoopCondition(json: String, index: Int) = index < json.length && json[index] in " \t\r\n"

  private fun jumpWhitespace(content: JsonParseContent) {
    @Suppress("ControlFlowWithEmptyBody")
    if (jumpWhitespaceLoopCondition(content.json, content.index)) while (jumpWhitespaceLoopCondition(content.json, ++content.index));
  }

  private fun charToInt(char: Char): Int {
    val indexOf = char - '0'
    if (indexOf < 0 || indexOf > 9) throw JsonFormatException("$char is not an number")
    return indexOf
  }

  private fun parseInt(content: JsonParseContent): Number {
    var number = charToInt(content.json[content.index]).toLong()
    while (++content.index < content.json.length && content.json[content.index] in '0'..'9') {
      number = number * 10 + charToInt(content.json[content.index])
    }
    return if (number <= Int.MAX_VALUE) number.toInt() else number
  }

  private fun parseNumber(content: JsonParseContent): Number {
    val negative = content.json[content.index] == '-'
    if (negative || content.json[content.index] == '+') content.index++
    var number: Number = when (content.json[content.index]) {
      in '0'..'9' -> parseInt(content)
      else -> throw JsonFormatException(content)
    }
    if (content.index < content.json.length && content.json[content.index] == '.') {
      if (++content.index >= content.json.length) throw JsonFormatException(content)
      var base = 0.1
      var double = charToInt(content.json[content.index]) * base
      while (++content.index < content.json.length && content.json[content.index] in '0'..'9') {
        base *= 0.1
        double += charToInt(content.json[content.index]) * base
      }
      number = number.toDouble() + double
    }
    if (content.index < content.json.length && content.json[content.index] in "eE") {
      val powerNegative = when (content.json[++content.index]) {
        '-' -> true
        '+' -> false
        else -> {
          content.index--
          false
        }
      }
      content.index++
      number = number.toDouble() * power(10.0, parseInt(content).toLong() * if (powerNegative) -1.0 else 1.0)
    }
    return if (negative) when (number) {
      is Int -> -number
      is Long -> -number
      else -> -number.toDouble()
    } else number
  }

  private fun parseString(content: JsonParseContent): String {
    if (content.json[content.index++] != '"') throw JsonFormatException("string not begin with '\"'")
    val builder = StringBuilder()
    while (content.index < content.json.length) when (content.json[content.index]) {
      '\\' -> {
        when (content.json[++content.index]) {
          'b' -> builder.append('\b')
          'f' -> builder.append('\u000C')
          'n' -> builder.append('\n')
          'r' -> builder.append('\r')
          't' -> builder.append('\t')
          'u' -> {
            var char = 0
            repeat(4) {
              val indexOf = "0123456789abcdef".indexOf(content.json[++content.index].toLowerCase())
              if (indexOf < 0) throw JsonFormatException(content)
              char = char * 16 + indexOf
            }
            builder.append(char.toChar())
          }
          else -> builder.append(content.json[content.index])
        }
        content.index++
      }
      '"' -> {
        content.index++
        return builder.toString()
      }
      else -> builder.append(content.json[content.index++])
    }
    throw JsonFormatException(content)
  }

  private fun parseObj(content: JsonParseContent): Map<String, Any?> {
    if (content.json[content.index++] != '{') throw JsonFormatException(content)
    jumpWhitespace(content)
    if (content.json[content.index] == '}') {
      content.index++
      return emptyMap()
    }
    val map = HashMap<String, Any?>()
    while (true) {
      jumpWhitespace(content)
      val key = parseString(content)
      jumpWhitespace(content)
      if (content.json[content.index++] != ':') throw JsonFormatException(content)
      map[key] = parse(content)
      jumpWhitespace(content)
      when (content.json[content.index++]) {
        ',' -> continue
        '}' -> break
        else -> throw JsonFormatException("json object not ends with '}'")
      }
    }
    return map
  }

  private fun parseArray(content: JsonParseContent): List<Any?> {
    if (content.json[content.index++] != '[') throw JsonFormatException(content)
    jumpWhitespace(content)
    if (content.json[content.index] == ']') {
      content.index++
      return emptyList()
    }
    val array = ArrayList<Any?>()
    while (true) {
      array.add(parse(content))
      jumpWhitespace(content)
      if (content.index >= content.json.length) throw JsonFormatException(content)
      when (content.json[content.index++]) {
        ',' -> continue
        ']' -> break
        else -> throw JsonFormatException(content)
      }
    }
    return array
  }
}

//fun main() {
//  println(Json.parse("   null   "))
//  println(Json.parse("   true   "))
//  println(Json.parse("   false   "))
//  println(Json.parse("   123   "))
//  println(Json.parse("   -123   "))
//  println(Json.parse("   123.0   "))
//  println(Json.parse("   123.0   "))
//  println(Json.parse("   123e2   "))
//  println(Json.parse("   123e10   "))
//  println(Json.parse("   -123.5e10   "))
//  println(Json.parse("   \"bb-12\\t3\\\".5e10aa\"   "))
//  println(Json.parse("   {}   "))
//  println(Json.parse("   {\"a\":3, \"c\": {}}   "))
//  println(Json.parse("[1,3, 4 ,\"cc\\u0041\"  , true , false ,  null , {}, {\"a\":\"b\"} , [   ] , [] ,    {\"a\":3, \"c\": {}, \"b\":[]}]"))
//  println(Json.parse("   [1,3,4]   "))
//}