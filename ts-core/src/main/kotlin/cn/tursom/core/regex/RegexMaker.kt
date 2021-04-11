package cn.tursom.core.regex

/**
 * 使用 regex 函数创建一个正则对象
 *
 * 字符串前加 + 表示一个字符串单元，任何时候都会作为一个独立单元存在
 * 字符串前加 - 表示一个字符串，不会作为一个独立单元处理
 * 注意，我们不支持原始的字符串对象，请使用 + 或 - 将其打包
 *
 * 在 RegexMaker 对象头部的这些对象都是字符转义，请根据需要使用
 * uppercase，lowercase 与 numbers 都是字符列表，用于表示在其范围内的单个字符
 * 使用 Char.control 获得控制字符转义
 *
 * 接下来是连接两个正则单元用的方法，我们可以用 (单元1) link (单元2)，(单元1) also (单元2)，甚至是 (单元1)(单元2) 的形式连接两个单元
 * 当然，现在我们也可以用 + 甚至是 - 来连接两个单元了
 *
 * 接着是创建单元组的方法，toSet 不建议使用，建议使用 ((单元1) or (单元2) or ...) 的形式创建一个单元组
 *
 * 后面跟着的就都是表示重复次数的方法，(单元)-次数n 表示最多重复n次，相应的 * 与 .. 表示精确的 n 次，%表示至少 n 次
 * 我们还可以使用 (单元)-(min..max)的形式指定重复区间，对于这个接口，-、*、% 与 .. 的效果相同，范围区间还可以使用(min to max)的形式
 * 有时候，- 会导致运算符优先级的问题，这时我们可以用 / 来代替
 *
 * 如果我们想匹配属于某一组的单个字符，可以使用 (开始字符 % 结束字符) 的形式，使用 and、also、link 或者 + 将多个字符组单元相连
 * 我们还可以在一个 CharRange 或 Pair<Char, Char> 前面加 + 生成字符组
 * 或者我们也可以手动指定使用哪些字符，使用 list 方法，字符串会被转义成一个字符组单元
 * 如果你对自己足够有信心，也可以在字符串前面加 !，这会直接生成一个字符组对象，不经检查
 *
 * 比如下面这句就是一个合法的表达式
 * '1' % '2' / 5 + list("ABC-\\") * lowercase * numbers % (2 to 3) + uppercase + any % 3 + caret % 1 + +"还行" * 2
 * 运行后会生成
 * [1-2]{0,5}[ABC\-\\a-z0-9]{2,3}[A-Z].{3,}\^+(还行){2}
 */
@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode")
object RegexMaker {
  operator fun String.unaryPlus() = StringRegexUnit(this)
  operator fun String.unaryMinus() = UnitRegexUnit(this)
  operator fun RegexUnit.unaryPlus() = GetMatchingUnit(this)
  operator fun RegexUnit.unaryMinus() = NonGetMatchingUnit(this)

  val Any.str
    get() = +toString()
  val Any.unit
    get() = -toString()

  val slush = -"\\\\"
  val point = -"\\."
  val caret = -"\\^"
  val dollar = -"\\$"
  val plus = -"\\+"
  val minus = -"\\-"
  val star = -"\\*"
  val roundBrackets = -"\\("
  val squareBrackets = -"\\["
  val curlyBrackets = -"\\{"
  val backslash = -"\\\\"
  val verticalBar = -"\\|"
  val questionMark = -"\\?"
  val nextPage = -"\\f"
  val nextLine = -"\\n"
  val enter = -"\\r"
  val blankCharacter = -"\\s"
  val nonBlankCharacter = -"\\S"
  val tab = -"\\t"
  val verticalTab = -"\\v"
  val wordBoundary = -"\\b"
  val nonWordBoundary = -"\\B"
  val number = -"\\d"
  val nonNumber = -"\\D"
  val pageBreak = -"\\f"
  val lineBreak = -"n"
  val carriageReturn = -"\\r"
  val lettersNumbersUnderscores = -"\\w"
  val nonLettersNumbersUnderscores = -"\\W"

  /**
   * @warning except \n
   */
  val any = -"."
  val beg = -"^"
  val begin = -"^"
  val end = -"$"
  val empty = -"()"

  val uppercase = 'A' % 'Z'
  val lowercase = 'a' % 'z'
  val numbers = '0' % '9'

  val Char.control
    get() = ControlCharRegexUnit(this)

  infix fun RegexUnit.link(target: RegexUnit) = +"$this$target"
  infix fun RegexUnit.link(target: (() -> RegexUnit)) = +"$this${target()}"
  infix fun (() -> RegexUnit).link(target: RegexUnit) = +"${this()}$target"
  infix fun (() -> RegexUnit).link(target: (() -> RegexUnit)) = +"${this()}${target()}"
  infix fun RegexUnit.also(target: RegexUnit) = this link target
  infix fun RegexUnit.also(target: (() -> RegexUnit)) = this link target
  infix fun (() -> RegexUnit).also(target: RegexUnit) = this link target
  infix fun (() -> RegexUnit).also(target: (() -> RegexUnit)) = this link target
  infix operator fun RegexUnit.invoke(unit: RegexUnit) = this link unit
  infix operator fun RegexUnit.invoke(unit: () -> RegexUnit) = this link unit
  infix operator fun (() -> RegexUnit).invoke(unit: () -> RegexUnit) = this link unit
  infix operator fun (() -> RegexUnit).invoke(unit: RegexUnit) = this link unit
  infix operator fun RegexUnit.plus(unit: RegexUnit) = this link unit
  infix operator fun RegexUnit.plus(unit: () -> RegexUnit) = this link unit
  infix operator fun (() -> RegexUnit).plus(unit: () -> RegexUnit) = this link unit
  infix operator fun (() -> RegexUnit).plus(unit: RegexUnit) = this link unit
  infix operator fun RegexUnit.minus(unit: RegexUnit) = this link unit
  infix operator fun RegexUnit.minus(unit: () -> RegexUnit) = this link unit
  infix operator fun (() -> RegexUnit).minus(unit: RegexUnit) = this link unit
  infix operator fun (() -> RegexUnit).minus(unit: () -> RegexUnit) = this link unit
  infix operator fun RegexUnit.rangeTo(unit: RegexUnit) = this link unit
  infix operator fun RegexUnit.rangeTo(unit: () -> RegexUnit) = this link unit
  infix operator fun (() -> RegexUnit).rangeTo(unit: () -> RegexUnit) = this link unit
  infix operator fun (() -> RegexUnit).rangeTo(unit: RegexUnit) = this link unit

  val Iterable<RegexUnit>.toSet: StringRegexUnit?
    get() {
      val iterator = iterator()
      if (!iterator.hasNext()) return null
      val stringBuilder = StringBuilder()
      stringBuilder.append(iterator.next().unit)
      forEach {
        stringBuilder.append("|${it.unit}")
      }
      return StringRegexUnit(stringBuilder.toString())
    }

  val Array<out RegexUnit>.toSet: StringRegexUnit?
    get() {
      val iterator = iterator()
      if (!iterator.hasNext()) return null
      val stringBuilder = StringBuilder()
      stringBuilder.append(iterator.next().unit)
      forEach {
        stringBuilder.append("|${it.unit}")
      }
      return StringRegexUnit(stringBuilder.toString())
    }

  infix fun RegexUnit.or(target: RegexUnit): StringRegexUnit {
    val unit = this.unit
    val targetUnit = target.unit
    return +when {
      unit == null -> targetUnit ?: ""
      targetUnit == null -> unit
      else -> "$unit|$targetUnit"
    }
  }

  infix fun (() -> RegexUnit).or(target: RegexUnit) = this() or target

  val RegexUnit.onceMore
    get() = RepeatRegexUnit(this, 1, -1)
  val (() -> RegexUnit).onceMore
    get() = RepeatRegexUnit(this(), 1, -1)

  val RegexUnit.anyTime
    get() = RepeatRegexUnit(this, -1)
  val (() -> RegexUnit).anyTime
    get() = RepeatRegexUnit(this(), -1)

  val RegexUnit.onceBelow
    get() = RepeatRegexUnit(this, 0, 1)
  val (() -> RegexUnit).onceBelow
    get() = RepeatRegexUnit(this(), 0, 1)

  infix fun RegexUnit.repeat(times: Int) = RepeatRegexUnit(this, times)
  infix fun RegexUnit.repeat(times: IntRange) = RepeatRegexUnit(this, times)
  infix fun RegexUnit.repeat(times: Pair<Int, Int>) = RepeatRegexUnit(this, times)
  fun RegexUnit.timeRange(from: Int, to: Int) = RepeatRegexUnit(this, from, to)

  infix fun (() -> RegexUnit).repeat(times: Int) = RepeatRegexUnit(this(), times)
  infix fun (() -> RegexUnit).repeat(times: IntRange) = RepeatRegexUnit(this(), times)
  infix fun (() -> RegexUnit).repeat(times: Pair<Int, Int>) = RepeatRegexUnit(this(), times)
  fun (() -> RegexUnit).timeRange(from: Int, to: Int) = RepeatRegexUnit(this(), from, to)

  infix operator fun RegexUnit.invoke(times: Int) = this repeat times
  infix operator fun RegexUnit.invoke(times: IntRange) = this repeat times
  infix operator fun RegexUnit.invoke(times: Pair<Int, Int>) = this repeat times
  operator fun RegexUnit.invoke(from: Int, to: Int) = this.timeRange(from, to)

  infix operator fun (() -> RegexUnit).invoke(unit: Int) = this()(unit)
  infix operator fun (() -> RegexUnit).invoke(unit: IntRange) = this()(unit)
  infix operator fun (() -> RegexUnit).invoke(unit: Pair<Int, Int>) = this()(unit)
  operator fun (() -> RegexUnit).invoke(from: Int, to: Int) = this().timeRange(from, to)

  infix fun RegexUnit.upTo(times: Int) = RepeatRegexUnit(this, 0, times)
  infix fun RegexUnit.repeatTime(times: Int) = RepeatRegexUnit(this, times)
  infix fun RegexUnit.repeatLast(times: Int) = RepeatRegexUnit(this, times, -1)
  infix fun RegexUnit.last(times: Int) = RepeatRegexUnit(this, times, -1)

  infix fun (() -> RegexUnit).upTo(times: Int) = this() upTo times
  infix fun (() -> RegexUnit).repeatTime(times: Int) = this() repeatTime times
  infix fun (() -> RegexUnit).repeatLast(times: Int) = this() repeatLast times
  infix fun (() -> RegexUnit).last(times: Int) = this() last times

  infix operator fun RegexUnit.rem(times: Int) = RepeatRegexUnit(this, times, -1)
  infix operator fun RegexUnit.times(times: Int) = RepeatRegexUnit(this, times)
  infix operator fun RegexUnit.minus(times: Int) = RepeatRegexUnit(this, 0, times)
  infix operator fun RegexUnit.div(times: Int) = RepeatRegexUnit(this, 0, times)
  infix operator fun RegexUnit.rangeTo(range: Int) = RepeatRegexUnit(this, range)

  infix operator fun (() -> RegexUnit).rem(times: Int) = this() rem times
  infix operator fun (() -> RegexUnit).times(times: Int) = this() times times
  infix operator fun (() -> RegexUnit).minus(times: Int) = this() minus times
  infix operator fun (() -> RegexUnit).div(times: Int) = this() div times
  infix operator fun (() -> RegexUnit).rangeTo(range: Int) = this() rangeTo range

  infix operator fun RegexUnit.rem(range: Pair<Int, Int>) = RepeatRegexUnit(this, range)
  infix operator fun RegexUnit.rem(range: IntRange) = RepeatRegexUnit(this, range)
  infix operator fun RegexUnit.times(range: IntRange) = RepeatRegexUnit(this, range)
  infix operator fun RegexUnit.times(range: Pair<Int, Int>) = RepeatRegexUnit(this, range)
  infix operator fun RegexUnit.minus(range: IntRange) = RepeatRegexUnit(this, range)
  infix operator fun RegexUnit.minus(range: Pair<Int, Int>) = RepeatRegexUnit(this, range)
  infix operator fun RegexUnit.rangeTo(range: IntRange) = RepeatRegexUnit(this, range)
  infix operator fun RegexUnit.rangeTo(range: Pair<Int, Int>) = RepeatRegexUnit(this, range)

  infix operator fun (() -> RegexUnit).rem(range: Pair<Int, Int>) = this() rem range
  infix operator fun (() -> RegexUnit).rem(range: IntRange) = this() rem range
  infix operator fun (() -> RegexUnit).times(range: IntRange) = this() times range
  infix operator fun (() -> RegexUnit).times(range: Pair<Int, Int>) = this() times range
  infix operator fun (() -> RegexUnit).minus(range: IntRange) = this() minus range
  infix operator fun (() -> RegexUnit).minus(range: Pair<Int, Int>) = this() minus range
  infix operator fun (() -> RegexUnit).rangeTo(range: IntRange) = this() rangeTo range
  infix operator fun (() -> RegexUnit).rangeTo(range: Pair<Int, Int>) = this() rangeTo range

  infix fun Char.list(target: Char) = UnitListRegexUnit(this, target)
  infix operator fun Char.rem(char: Char) = UnitListRegexUnit(this, char)
  operator fun CharRange.unaryPlus() = UnitListRegexUnit(this)
  operator fun CharRange.unaryMinus() = UnitListRegexUnit(this)
  operator fun CharRange.not() = UnitListRegexUnit(this)
  operator fun Pair<Char, Char>.unaryPlus() = UnitListRegexUnit(this)
  operator fun Pair<Char, Char>.unaryMinus() = UnitListRegexUnit(this)
  operator fun Pair<Char, Char>.not() = UnitListRegexUnit(this)

  infix operator fun UnitListRegexUnit.invoke(unitList: UnitListRegexUnit) = this and unitList

  object UnitList {
    const val hyphen = "\\-"
    const val slush = "\\\\"
    operator fun invoke(action: UnitList.() -> Any) = !this.action()
  }

  class UnitListCheckException : Exception()

  operator fun String.not() = UnitListRegexUnit(this)
  operator fun Any.not() = !toString()
  private val listChar = Regex("[-\\\\]")

  /**
   * 获取str的字面符号表示的字符表
   */
  fun list(str: String): UnitListRegexUnit {
    if (!listChar.containsMatchIn(str)) {
      return !str
    }
    val sb = StringBuilder()
    str.forEach { c ->
      when (c) {
        '\\', '-', ':' -> sb.append("\\")
      }
      sb.append(c)
    }
    return !sb
  }

  val RegexUnit.nonGetMatch get() = NonGetMatchingUnit(this)
  val RegexUnit.lookAheadPositiveAssert get() = LookAheadPositiveAssertUnit(this)
  val RegexUnit.lookAheadNegativeAssert get() = LookAheadNegativeAssertUnit(this)
  val RegexUnit.lookBehindPositiveAssert get() = LookBehindPositiveAssertUnit(this)
  val RegexUnit.lookBehindNegativeAssert get() = LookBehindNegativeAssertUnit(this)

  fun make(func: RegexMaker.() -> RegexUnit) = this.func()

  operator fun invoke(func: RegexMaker.() -> RegexUnit) = this.func()
}

fun regex(func: RegexMaker.() -> RegexUnit) = Regex(RegexMaker.func().toString())