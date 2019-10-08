package cn.tursom.core.regex

class GetMatchingUnit(val subUnit: RegexUnit) : RegexUnit {
    override val unit: String?
        get() = toString()

    override fun toString(): String = "($subUnit)"
}