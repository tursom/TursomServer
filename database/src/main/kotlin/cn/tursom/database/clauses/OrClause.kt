package cn.tursom.database.clauses

class OrClause(first: Clause, second: Clause) : Clause {
	override val sqlStr = "(${first.sqlStr} OR ${second.sqlStr})"
	override fun toString() = sqlStr
}