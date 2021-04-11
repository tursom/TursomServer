package cn.tursom.core.regex

class UnitListRegexUnit(private val valList: String) : RegexUnit {
  constructor(from: Char, to: Char) : this("$from-$to")
  constructor(range: Pair<Char, Char>) : this("${range.first}-${range.second}")
  constructor(range: CharRange) : this("${range.first}-${range.last}")
  constructor(range: UnitListRegexUnit) : this(range.valList)

  val reverse
    get() = UnitListRegexUnit(if (valList.first() == '^') valList.drop(1) else "^$valList")

  operator fun not() = reverse
  infix operator fun plus(unitList: UnitListRegexUnit) = UnitListRegexUnit("$valList${unitList.valList}")
  infix operator fun times(unitList: UnitListRegexUnit) = UnitListRegexUnit("$valList${unitList.valList}")
  infix fun also(unitList: UnitListRegexUnit) = UnitListRegexUnit("$valList${unitList.valList}")
  infix fun and(unitList: UnitListRegexUnit) = UnitListRegexUnit("$valList${unitList.valList}")
  infix fun link(unitList: UnitListRegexUnit) = UnitListRegexUnit("$valList${unitList.valList}")

  override fun toString() = "[$valList]"
}