package cn.tursom.core.regex

class ControlCharRegexUnit(private val char: Char) : RegexUnit {
  constructor(char: ControlCharRegexUnit) : this(char.char)

  override fun toString() = "\\c$char"
}