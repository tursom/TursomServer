package cn.tursom.core.regex

class LookBehindNegativeAssertUnit(val subUnit: RegexUnit) : RegexUnit {
  override fun toString(): String = "(?<!$subUnit)"
}