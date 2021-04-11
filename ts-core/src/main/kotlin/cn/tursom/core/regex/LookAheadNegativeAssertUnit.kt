package cn.tursom.core.regex

class LookAheadNegativeAssertUnit(val subUnit: RegexUnit) : RegexUnit {
  override fun toString(): String = "(?=$subUnit)"
}