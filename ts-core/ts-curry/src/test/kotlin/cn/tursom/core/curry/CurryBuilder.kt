package com.ddbes.kotlin

import com.ddbes.kotlin.util.removeLastChars
import org.junit.Test

class CurryBuilder {
  @Test
  fun buildCurryClass() {
    (3..20).forEach(::buildCurryClass)
  }

  @Test
  fun buildInvokeMethod() {
    (3..20).forEach(::buildInvokeMethod)
  }

  @Test
  fun buildMethodInvoker() {
    (4..20).forEach(::buildMethodInvoker)
  }

  fun args(index: Int) = buildString {
    repeat(index) {
      append("a${it + 1}, ")
    }
    removeLastChars(2)
  }

  fun args(fromIndex: Int, toIndex: Int) = buildString {
    (fromIndex..toIndex).forEach {
      append("a${it}, ")
    }
    removeLastChars(2)
  }

  fun types(index: Int) = buildString {
    repeat(index) {
      append("T${it + 1}, ")
    }
    removeLastChars(2)
  }

  fun types(fromIndex: Int, toIndex: Int) = buildString {
    (fromIndex..toIndex).forEach {
      append("T${it}, ")
    }
    removeLastChars(2)
  }

  fun argsWithType(index: Int) = buildString {
    repeat(index) {
      append("a${it + 1}: T${it + 1}, ")
    }
    removeLastChars(2)
  }

  fun buildCurryClass(index: Int) {
    val args = args(index)
    val argsWithType = argsWithType(index)
    val types = types(index)
    val invokeAction = "action$index($args)"
    println(buildString {
      append("open class Curry$index<$types, R>(\n")
      append("    val action$index: ($argsWithType) -> R,\n")
      append(") : Curry${index - 1}<${types(index - 1)}, Curry1<T$index, R>>({ ${args(index - 1)} ->\n")
      append("    Curry1 { a$index ->\n")
      append("        action$index($args)\n")
      append("    }\n")
      append("}) {\n")
      if (index > 2) (1..index - 2).forEach { overrideInvoke ->
        append(
          "    override operator fun invoke(${argsWithType(overrideInvoke)}): Curry${index - overrideInvoke}<${
            types(overrideInvoke + 1, index)
          }, R> = Curry${index - overrideInvoke} { ${args(overrideInvoke + 1, index)} ->\n"
        )
        append("        $invokeAction\n")
        append("    }\n")
        append("\n")
      }
      append("    open operator fun invoke($argsWithType): R = $invokeAction\n")
      append("}\n")
    })
  }

  fun buildCurryMethod(index: Int) {
    val args = args(index)
    val argsWithType = argsWithType(index)
    val types = types(index)

    println(buildString {
      append("fun <$types, R> curry(action: ($types) -> R) =\n")
      append("    Curry$index { $argsWithType ->\n")
      append("        action($args)\n")
      append("    }\n")
    })
  }

  fun buildInvokeMethod(index: Int) {
    val args = args(index)
    val argsWithType = argsWithType(index)
    val types = types(index)

    println(buildString {
      append("operator fun <$types, R> ")
      repeat(index - 1) {
        append("Curry1<T${it + 1}, ")
      }
      append("Curry1<T$index, R>", ">".repeat(index - 1), ".invoke($argsWithType): R {\n")
      append("    return if (this is Curry$index) {\n")
      append("        uncheckedCast<Curry$index<$types, R>>()($args)\n")
      append("    } else {\n")
      append("        invoke")
      repeat(index) { an ->
        append("(a${an + 1})")
      }
      append("\n")
      append("    }\n")
      append("}\n")
    })
  }

  fun buildMethodInvoker(index: Int) {
    val args = args(index)
    val argsWithType = argsWithType(index)
    val types = types(index)

    println("operator fun <$types, R> (($types) -> R).invoke() = curry(this)")
    (1 until index).forEach { argCount ->
      println(
        "operator fun <$types, R> (($types) -> R).invoke(${argsWithType(argCount)}) = " +
          "curry(this)(${args(argCount)})"
      )
    }
    println()
  }
}
