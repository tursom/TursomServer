package cn.tursom.core.regex

class StringRegexUnit(private val str: String) : RegexUnit {
  constructor(str: StringRegexUnit) : this(str.str)

  override val unit = when (str.length) {
    0 -> null
    1 -> str
    else -> "($str)"
  }

  override fun toString() = str
}