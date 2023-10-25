package cn.tursom.core.curry

import cn.tursom.core.util.removeLastChars
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

  @Test
  fun buildObjectI() {
    println(
      "package com.ddbes.kotlin.util\n" +
        "\n" +
        "import com.ddbes.kotlin.util.curry.*\n"
    )
    println(buildObjectI(20, "I", extensionLambda = false, extensionCurry = false))
    println(buildObjectI(20, "F", extensionLambda = true, extensionCurry = false))
    //println(buildObjectI(20, "C", extensionLambda = false, extensionCurry = true))
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

  fun argsWithType(fromIndex: Int, toIndex: Int) = buildString {
    (fromIndex..toIndex).forEach {
      append("a$it: T$it, ")
    }
    removeLastChars(2)
  }

  fun buildObjectI(
    index: Int,
    objectName: String = "I",
    extensionLambda: Boolean = true,
    extensionCurry: Boolean = true,
  ) = buildString {
    append("object $objectName {\n")
    append("    inline operator fun <R> invoke(f: () -> R) = f()\n")
    if (extensionLambda || extensionCurry) append("\n")
    (1..index).forEach {
      append(
        "    inline operator fun <${types(it)}, R> invoke(f: (${types(it)}) -> R, ${
          argsWithType(it)
        }) = f(${args(it)})\n"
      )
      if (extensionCurry) append(
        "operator fun <${types(it)}, R> invoke(f: Curry${it}<${types(it)}, R>, ${
          argsWithType(it)
        }) = f(${args(it)})\n"
      )
      repeat(it - 1) { curry ->
        if (extensionLambda) append(
          "operator fun <${types(it)}, R> invoke(f: (${types(it)}) -> R, ${
            argsWithType(it - curry - 1)
          }) = f(${args(it - curry - 1)})\n"
        )
        if (extensionCurry) append(
          "operator fun <${types(it)}, R> invoke(f: Curry${it}<${types(it)}, R>, ${
            argsWithType(it - curry - 1)
          }) = f(${args(it - curry - 1)})\n"
        )
      }
      if (extensionLambda || extensionCurry) append("\n")
    }
    append("}\n")
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
        append("    override operator fun invoke(${argsWithType(overrideInvoke)}): Curry${index - overrideInvoke}<${
          types(overrideInvoke + 1, index)
        }, R> = Curry${index - overrideInvoke} { ${args(overrideInvoke + 1, index)} ->\n")
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
    val types = types(index)

    println("operator fun <$types, R> (($types) -> R).invoke() = this")
    (1 until index).forEach { argCount ->
      println(
        "operator fun <$types, R> (($types) -> R).invoke(${argsWithType(argCount)}) = " +
          "{ ${argsWithType(argCount + 1, index)} -> this(${args}) }"
      )
    }
    println()
  }
}
