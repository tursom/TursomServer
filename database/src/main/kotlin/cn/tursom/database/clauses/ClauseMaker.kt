package cn.tursom.database.clauses

import cn.tursom.database.SqlUtils.sqlStr
import cn.tursom.database.SqlUtils.fieldName
import cn.tursom.core.regex.RegexMaker
import cn.tursom.core.regex.RegexUnit
import java.lang.reflect.Field
import kotlin.reflect.KProperty

/**
 * 我想有点kotlin基础的人都能看懂怎么用的
 * 所有我就不写说明书了，只举个例子
 * clause { (!TestClass::text regexp { beg + +"还行" + end }) or (!TestClass::_id equal "10") }
 */
object ClauseMaker {
	//	operator fun Field.unaryMinus() = fieldName
//	operator fun KProperty<*>.unaryMinus() = fieldName
//	operator fun Field.unaryPlus() = fieldName
//	operator fun KProperty<*>.unaryPlus() = fieldName
	operator fun Any.not() = this.toString()

	operator fun String.not() = this.sqlStr
	operator fun Field.not() = fieldName
	operator fun KProperty<*>.not() = fieldName

	infix fun Clause.and(clause: Clause) = AndClause(this, clause)
	infix operator fun Clause.plus(clause: Clause) = AndClause(this, clause)
	infix fun String.equal(value: String) = EqualClause(this, value)
	infix fun String.glob(value: String) = GlobClause(this, value)
	infix fun String.glob(maker: GlobClause.GlobValue.() -> String) = GlobClause(this, GlobClause.GlobValue.maker())
	infix fun String.greaterEqual(value: String) = GreaterEqualClause(this, value)
	infix fun String.greaterThan(value: String) = GreaterThanClause(this, value)
	infix fun String.lessEqual(value: String) = LessEqualClause(this, value)
	infix fun String.lessThan(value: String) = LessThanClause(this, value)
	infix fun String.like(value: String) = LikeClause(this, value)
	infix fun String.like(value: LikeClause.LikeWildcard.() -> String) = LikeClause(this, value)
	operator fun Clause.not() = NotClause(this)
	infix fun String.notEqual(value: String) = NotEqualClause(this, value)
	infix fun Clause.or(value: Clause) = OrClause(this, value)
	infix operator fun Clause.minus(value: Clause) = OrClause(this, value)
//	infix operator fun Clause.rangeTo(value: Clause) = OrClause(this, value)

	infix fun String.regexp(value: String) = RegexpClause(this, value)
	infix fun String.regexp(value: Regex) = RegexpClause(this, value)
	infix fun String.regexp(value: RegexUnit) = RegexpClause(this, value)
	infix fun String.regexp(value: RegexMaker.() -> RegexUnit) = RegexpClause(this, value)

	@Suppress("UNUSED_EXPRESSION")
	fun make(maker: ClauseMaker.() -> Clause) = maker()

	inline operator fun invoke(maker: ClauseMaker.() -> Clause) = this.maker().sqlStr
}

fun clause(maker: ClauseMaker.() -> Clause) = ClauseMaker.maker()