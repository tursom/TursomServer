package cn.tursom.core.regex

class LookAheadPositiveAssertUnit(val subUnit: RegexUnit) : RegexUnit {
    override fun toString(): String = "(?=$subUnit)"
}