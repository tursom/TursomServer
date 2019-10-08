package cn.tursom.database.clauses

class AndClause(first: Clause, second: Clause) : Clause {
	override val sqlStr = "(${first.sqlStr} AND ${second.sqlStr})"
	override fun toString() = sqlStr
}