package cn.tursom.core.regex

/**
 * 将 ${repeatUnit} 匹配 ${from} 到 ${to} 次
 * 匹配任意次数 如果 ${from} < 0
 * 精确匹配 ${from} 次 如果 ${to} == 0
 * 最少匹配 ${from} 次 如果 ${to} < 0
 */
class RepeatRegexUnit(repeatUnit: RegexUnit?, from: Int, to: Int = 0) : RegexUnit {
	constructor(repeatUnit: RegexUnit?, range: IntRange) : this(repeatUnit, range.start, range.last)
	constructor(repeatUnit: RegexUnit?, range: Pair<Int, Int>) : this(repeatUnit, range.first, range.second)
	
	private val str = when {
		from < 0 -> "*"
		to == 0 -> when (from) {
			0 -> null
			1 -> ""
			else -> "{$from}"
		}
		to < 0 -> when (from) {
			0 -> "*"
			1 -> "+"
			else -> "{$from,}"
		}
		to == 1 && from == 0 -> "?"
		to == from -> when (from) {
			0 -> null
			1 -> ""
			else -> "{$from}"
		}
		else -> "{$from,$to}"
	}?.let { range ->
		repeatUnit?.unit?.let {
			if (it.isNotEmpty()) "$it$range"
			else ""
		}
	} ?: ""
	
	override val unit = if (str.isEmpty()) {
		""
	} else {
		"($str)"
	}
	
	override fun toString() = str
}