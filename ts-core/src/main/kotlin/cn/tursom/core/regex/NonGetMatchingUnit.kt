package cn.tursom.core.regex

class NonGetMatchingUnit(val subUnit: RegexUnit) : RegexUnit {
  override fun toString(): String = "(?:$subUnit)"
}