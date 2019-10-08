package cn.tursom.core.regex

class LookBehindPositiveAssertUnit(val subUnit: RegexUnit) : RegexUnit {
    override fun toString(): String = "(?<=$subUnit)"
}