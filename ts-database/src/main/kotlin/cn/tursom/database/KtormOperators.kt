@file:Suppress("unused")

package cn.tursom.database

import org.ktorm.dsl.*
import org.ktorm.expression.*
import org.ktorm.schema.SqlType
import kotlin.reflect.KProperty1

inline fun <reified T : Any> KProperty1<T, *>.isNull(): UnaryExpression<Boolean> = sql.isNull()
inline fun <reified T : Any> KProperty1<T, *>.isNotNull(): UnaryExpression<Boolean> = sql.isNotNull()
inline operator fun <reified C : Any, T : Number> KProperty1<C, T?>.unaryMinus(): UnaryExpression<T> =
  table[this].unaryMinus()

inline operator fun <reified C : Any, T : Number> KProperty1<C, T?>.unaryPlus(): UnaryExpression<T> =
  table[this].unaryPlus()

inline operator fun <reified T : Any> KProperty1<T, Boolean>.not(): UnaryExpression<Boolean> = table[this].not()

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> KProperty1<C, T?>.plus(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  table[this].plus(expr.table[expr])

inline infix operator fun <reified C : Any, T : Number> KProperty1<C, T?>.plus(value: T): BinaryExpression<T> =
  table[this].plus(value)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> T.plus(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  plus(expr.table[expr])

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> KProperty1<C, T?>.minus(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  table[this].minus(expr.table[expr])

inline infix operator fun <reified C : Any, T : Number> KProperty1<C, T?>.minus(value: T): BinaryExpression<T> =
  table[this].minus(value)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> T.minus(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  minus(expr.table[expr])

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> KProperty1<C, T?>.times(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  table[this].times(expr.table[expr])

inline infix operator fun <reified C : Any, T : Number> KProperty1<C, T?>.times(value: T): BinaryExpression<T> =
  table[this].times(value)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> T.times(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  times(expr.table[expr])

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> KProperty1<C, T?>.div(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  table[this].div(expr.table[expr])

inline infix operator fun <reified C : Any, T : Number> KProperty1<C, T?>.div(value: T): BinaryExpression<T> =
  table[this].div(value)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> T.div(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  div(expr.table[expr])

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> KProperty1<C, T?>.rem(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  table[this].rem(expr.table[expr])

inline infix operator fun <reified C : Any, T : Number> KProperty1<C, T?>.rem(value: T): BinaryExpression<T> =
  table[this].rem(value)

inline infix operator fun <reified C : Any, reified C2 : Any, T : Number> T.rem(expr: KProperty1<C2, T?>): BinaryExpression<T> =
  rem(expr.table[expr])

inline infix fun <reified T : Any, reified T2 : Any> KProperty1<T, *>.like(expr: KProperty1<T2, String>): BinaryExpression<Boolean> =
  table[this].like(expr.table[expr])

inline infix fun <reified T : Any> KProperty1<T, *>.like(value: String): BinaryExpression<Boolean> =
  table[this].like(value)

inline infix fun <reified T : Any, reified T2 : Any> KProperty1<T, *>.notLike(expr: KProperty1<T2, String>): BinaryExpression<Boolean> =
  table[this].notLike(expr.table[expr])

inline infix fun <reified T : Any> KProperty1<T, *>.notLike(value: String): BinaryExpression<Boolean> =
  table[this].notLike(value)

inline infix fun <reified T : Any, reified T2 : Any> KProperty1<T, Boolean>.and(expr: KProperty1<T2, Boolean>): BinaryExpression<Boolean> =
  table[this].and(expr.table[expr])

inline infix fun <reified T : Any> KProperty1<T, Boolean>.and(value: Boolean): BinaryExpression<Boolean> =
  table[this].and(value)

inline infix fun <reified T : Any> Boolean.and(expr: KProperty1<T, Boolean>): BinaryExpression<Boolean> =
  and(expr.table[expr])

inline infix fun <reified T : Any, reified T2 : Any> KProperty1<T, Boolean>.or(expr: KProperty1<T2, Boolean>): BinaryExpression<Boolean> =
  table[this].or(expr.table[expr])

inline infix fun <reified T : Any> KProperty1<T, Boolean>.or(value: Boolean): BinaryExpression<Boolean> =
  table[this].or(value)

inline infix fun <reified T : Any> Boolean.or(expr: KProperty1<T, Boolean>): BinaryExpression<Boolean> =
  or(expr.table[expr])

inline infix fun <reified T : Any, reified T2 : Any> KProperty1<T, Boolean>.xor(expr: KProperty1<T2, Boolean>): BinaryExpression<Boolean> =
  table[this].xor(expr.table[expr])

inline infix fun <reified T : Any> KProperty1<T, Boolean>.xor(value: Boolean): BinaryExpression<Boolean> =
  table[this].xor(value)

inline infix fun <reified T : Any> Boolean.xor(expr: KProperty1<T, Boolean>): BinaryExpression<Boolean> =
  xor(expr.table[expr])

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> KProperty1<C, T?>.less(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  table[this].less(expr.table[expr])

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.less(value: T): BinaryExpression<Boolean> =
  table[this].less(value)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> T.less(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  less(expr.table[expr])

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> KProperty1<C, T?>.lessEq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  table[this].lessEq(expr.table[expr])

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.lessEq(value: T): BinaryExpression<Boolean> =
  table[this].lessEq(value)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> T.lessEq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  lessEq(expr.table[expr])

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> KProperty1<C, T?>.greater(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  table[this].greater(expr.table[expr])

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.greater(value: T): BinaryExpression<Boolean> =
  table[this].greater(value)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> T.greater(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  greater(expr.table[expr])

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> KProperty1<C, T?>.greaterEq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  table[this].greaterEq(expr.table[expr])

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.greaterEq(value: T): BinaryExpression<Boolean> =
  table[this].greaterEq(value)

inline infix fun <reified C : Any, reified C2 : Any, T : Comparable<T>> T.greaterEq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  greaterEq(expr.table[expr])

inline infix fun <reified C : Any, reified C2 : Any, T : Any> KProperty1<C, T?>.eq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  table[this].eq(expr.table[expr])

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.eq(value: T): BinaryExpression<Boolean> =
  table[this].eq(value)

inline infix fun <reified C : Any, reified C2 : Any, T : Any> KProperty1<C, T?>.notEq(expr: KProperty1<C2, T?>): BinaryExpression<Boolean> =
  table[this].notEq(expr.table[expr])

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.notEq(value: T): BinaryExpression<Boolean> =
  table[this].notEq(value)

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.between(range: ClosedRange<T>): BetweenExpression<T> =
  table[this].between(range)

inline infix fun <reified C : Any, T : Comparable<T>> KProperty1<C, T?>.notBetween(range: ClosedRange<T>): BetweenExpression<T> =
  table[this].notBetween(range)

inline fun <reified C : Any, T : Any> KProperty1<C, T?>.inList(vararg list: T): InListExpression<T> =
  table[this].inList(list = list)

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.inList(list: Collection<T>): InListExpression<T> =
  table[this].inList(list)

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.inList(query: Query): InListExpression<T> =
  table[this].inList(query)

inline fun <reified C : Any, T : Any> KProperty1<C, T?>.notInList(vararg list: T): InListExpression<T> =
  table[this].notInList(list = list)

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.notInList(list: Collection<T>): InListExpression<T> =
  table[this].notInList(list)

inline infix fun <reified C : Any, T : Any> KProperty1<C, T?>.notInList(query: Query): InListExpression<T> =
  table[this].notInList(query)

inline fun <reified C : Any> KProperty1<C, Number>.toDouble(): CastingExpression<Double> =
  table[this].toDouble()

inline fun <reified C : Any> KProperty1<C, Number>.toFloat(): CastingExpression<Float> =
  table[this].toFloat()

inline fun <reified C : Any> KProperty1<C, Number>.toInt(): CastingExpression<Int> =
  table[this].toInt()

inline fun <reified C : Any> KProperty1<C, Number>.toShort(): CastingExpression<Short> =
  table[this].toShort()

inline fun <reified C : Any> KProperty1<C, Number>.toLong(): CastingExpression<Long> =
  table[this].toLong()

@JvmName("booleanToInt")
inline fun <reified C : Any> KProperty1<C, Boolean>.toInt(): CastingExpression<Int> =
  table[this].toInt()

inline fun <reified C : Any, T : Any> KProperty1<C, *>.cast(sqlType: SqlType<T>): CastingExpression<T> =
  table[this].cast(sqlType)
