package cn.tursom.core.regex

class UnitRegexUnit(override val unit: String) : RegexUnit {
	constructor(unit: UnitRegexUnit) : this(unit.unit)
	
	override fun toString() = unit
}