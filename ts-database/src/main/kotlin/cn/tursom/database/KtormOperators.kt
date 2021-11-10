@file:Suppress("unused")

package cn.tursom.database

import org.ktorm.dsl.*
import org.ktorm.expression.*
import org.ktorm.schema.SqlType
import kotlin.reflect.KProperty1

inline fun <reified T : Any> KProperty1<T, *>.isNull(): UnaryExpression<Boolean> = sql.isNull()
inline fun <reified T : Any> KProperty1<T, *>.isNotNull(): UnaryExpression<Boolean> = sql.isNotNull()
inline operator fun <reified C : Any, T : Number> KProperty1<C, T?>.unaryMinus(): UnaryExpression<T> =
  sql.unaryMinus()

inline operator fun <reified C : Any, T : Number> KProperty1<C, T?>.unaryPlus(): UnaryExpression<T> =
  sql.unaryPlus()

inline operator fun <reified T : Any> KProperty1<T, Boolean?>.not(): UnaryExpression<Boolean> = sql.not()

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> KProperty1<C, T?>.plus(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  sql.plus(expr.sql)

inline infix operator fun <reified C : Any, T : Number> KProperty1<C, T?>.plus(value: T): BinaryExpression<T> =
  sql.plus(value)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> T.plus(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  plus(expr.sql)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> KProperty1<C, T?>.minus(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  sql.minus(expr.sql)

inline infix operator fun <reified C : Any, T : Number> KProperty1<C, T?>.minus(value: T): BinaryExpression<T> =
  sql.minus(value)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> T.minus(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  minus(expr.sql)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> KProperty1<C, T?>.times(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  sql.times(expr.sql)

inline infix operator fun <reified C : Any, T : Number> KProperty1<C, T?>.times(value: T): BinaryExpression<T> =
  sql.times(value)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> T.times(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  times(expr.sql)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> KProperty1<C, T?>.div(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  sql.div(expr.sql)

inline infix operator fun <reified C : Any, T : Number> KProperty1<C, T?>.div(value: T): BinaryExpression<T> =
  sql.div(value)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> T.div(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  div(expr.sql)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> KProperty1<C, T?>.rem(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  sql.rem(expr.sql)

inline infix operator fun <reified C : Any, T : Number> KProperty1<C, T?>.rem(value: T): BinaryExpression<T> =
  sql.rem(value)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> T.rem(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  rem(expr.sql)

inline infix fun <reified T : Any, reified T2 : Any> KProperty1<T, *>.like(expr: KProperty1<T2, String?>): BinaryExpression<Boolean> =
  sql.like(expr.sql)

inline infix fun <reified T : Any> KProperty1<T, *>.like(value: String): BinaryExpression<Boolean> =
  sql.like(value)

inline infix fun <reified T : Any, reified T2 : Any> KProperty1<T, *>.notLike(expr: KProperty1<T2, String?>): BinaryExpression<Boolean> =
  sql.notLike(expr.sql)

inline infix fun <reified T : Any> KProperty1<T, *>.notLike(value: String): BinaryExpression<Boolean> =
  sql.notLike(value)

inline infix fun <reified T : Any, reified T2 : Any> KProperty1<T, Boolean?>.and(expr: KProperty1<T2, Boolean?>): BinaryExpression<Boolean> =
  sql.and(expr.sql)

inline infix fun <reified T : Any> KProperty1<T, Boolean?>.and(value: Boolean): BinaryExpression<Boolean> =
  sql.and(value)

inline infix fun <reified T : Any> Boolean.and(expr: KProperty1<T, Boolean?>): BinaryExpression<Boolean> =
  and(expr.sql)

inline infix fun <reified T : Any, reified T2 : Any> KProperty1<T, Boolean?>.or(expr: KProperty1<T2, Boolean?>): BinaryExpression<Boolean> =
  sql.or(expr.sql)

inline infix fun <reified T : Any> KProperty1<T, Boolean?>.or(value: Boolean): BinaryExpression<Boolean> =
  sql.or(value)

inline infix fun <reified T : Any> Boolean.or(expr: KProperty1<T, Boolean?>): BinaryExpression<Boolean> =
  or(expr.sql)

inline infix fun <reified T : Any, reified T2 : Any> KProperty1<T, Boolean?>.xor(expr: KProperty1<T2, Boolean?>): BinaryExpression<Boolean> =
  sql.xor(expr.sql)

inline infix fun <reified T : Any> KProperty1<T, Boolean?>.xor(value: Boolean): BinaryExpression<Boolean> =
  sql.xor(value)

inline infix fun <reified T : Any> Boolean.xor(expr: KProperty1<T, Boolean?>): BinaryExpression<Boolean> =
  xor(expr.sql)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> KProperty1<C, T?>.less(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  sql.less(expr.sql)

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.less(value: T): BinaryExpression<Boolean> =
  sql.less(value)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> T.less(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  less(expr.sql)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> KProperty1<C, T?>.lessEq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  sql.lessEq(expr.sql)

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.lessEq(value: T): BinaryExpression<Boolean> =
  sql.lessEq(value)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> T.lessEq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  lessEq(expr.sql)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> KProperty1<C, T?>.greater(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  sql.greater(expr.sql)

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.greater(value: T): BinaryExpression<Boolean> =
  sql.greater(value)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> T.greater(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  greater(expr.sql)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> KProperty1<C, T?>.greaterEq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  sql.greaterEq(expr.sql)

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.greaterEq(value: T): BinaryExpression<Boolean> =
  sql.greaterEq(value)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> T.greaterEq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  greaterEq(expr.sql)

inline infix fun <reified C : Any, reified C2 : Any, T : Any> KProperty1<C, T?>.eq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  sql.eq(expr.sql)

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.eq(value: T): BinaryExpression<Boolean> =
  sql.eq(value)

inline infix fun <reified C : Any, reified C2 : Any, T : Any> KProperty1<C, T?>.notEq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  sql.notEq(expr.sql)

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.notEq(value: T): BinaryExpression<Boolean> =
  sql.notEq(value)

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.between(range: ClosedRange<T>): BetweenExpression<T> =
  sql.between(range)

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.notBetween(range: ClosedRange<T>): BetweenExpression<T> =
  sql.notBetween(range)

inline fun <reified C : Any, T : Any> KProperty1<C, T?>.inList(vararg list: T): InListExpression<T> =
  sql.inList(list = list)

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.inList(list: Collection<T>): InListExpression<T> =
  sql.inList(list)

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.inList(query: Query): InListExpression<T> =
  sql.inList(query)

inline fun <reified C : Any, T : Any> KProperty1<C, T?>.notInList(vararg list: T): InListExpression<T> =
  sql.notInList(list = list)

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.notInList(list: Collection<T>): InListExpression<T> =
  sql.notInList(list)

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.notInList(query: Query): InListExpression<T> =
  sql.notInList(query)

inline fun <reified C : Any> KProperty1<C, Number?>.toDouble(): CastingExpression<Double> =
  sql.toDouble()

inline fun <reified C : Any> KProperty1<C, Number?>.toFloat(): CastingExpression<Float> =
  sql.toFloat()

inline fun <reified C : Any> KProperty1<C, Number?>.toInt(): CastingExpression<Int> =
  sql.toInt()

inline fun <reified C : Any> KProperty1<C, Number?>.toShort(): CastingExpression<Short> =
  sql.toShort()

inline fun <reified C : Any> KProperty1<C, Number?>.toLong(): CastingExpression<Long> =
  sql.toLong()

@JvmName("booleanToInt")
inline fun <reified C : Any> KProperty1<C, Boolean?>.toInt(): CastingExpression<Int> =
  sql.toInt()

inline fun <reified C : Any, T : Any> KProperty1<C, *>.cast(sqlType: SqlType<T>): CastingExpression<T> =
  sql.cast(sqlType)
