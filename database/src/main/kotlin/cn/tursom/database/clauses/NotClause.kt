package cn.tursom.database.clauses

class NotClause(clause: Clause) : Clause {
	override val sqlStr = "(NOT ${clause.sqlStr})"
	override fun toString() = sqlStr
}