package cn.tursom.database.mybatisplus

import cn.tursom.core.uncheckedCast
import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.conditions.interfaces.Func
import kotlin.reflect.KProperty1

@Suppress("unused")
@MybatisPlusEnhanceDslMaker
interface FuncEnhance<T, out Children : Wrapper<T>> {
  val func: Func<out Children, String> get() = uncheckedCast()

  fun KProperty1<T, *>.having(
    vararg value: Any?,
  ): Children = func.having(getFieldData()!!.name, value)

  fun KProperty1<T, *>.isNull(column: KProperty1<T, *>): Children = func.isNull(column.getFieldData()!!.name)

  fun KProperty1<T, *>.isNotNull(column: KProperty1<T, *>): Children = func.isNotNull(column.getFieldData()!!.name)

  infix fun KProperty1<T, *>.`in`(
    value: Any?,
  ): Children = func.`in`(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.`in`(
    value: Collection<Any?>,
  ): Children = func.`in`(getFieldData()!!.name, value)

  fun KProperty1<T, *>.`in`(
    vararg value: Any,
  ): Children = func.`in`(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.notIn(
    value: Collection<Any?>,
  ): Children = func.notIn(getFieldData()!!.name, value)

  fun KProperty1<T, *>.notIn(
    vararg value: Any,
  ): Children = func.notIn(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.inSql(
    value: String?,
  ): Children = func.inSql(getFieldData()!!.name, value)

  infix fun KProperty1<T, *>.notInSql(
    value: String?,
  ): Children = func.notInSql(getFieldData()!!.name, value)


  fun groupBy(column: KProperty1<T, *>): Children = func.groupBy(column.getFieldData()!!.name)

  @Suppress("UNCHECKED_CAST")
  fun groupBy(vararg columns: KProperty1<T, *>): Children =
    func.groupBy(columns.map { column -> column.getFieldData()!!.name })

  fun orderByAsc(column: KProperty1<T, *>): Children = func.orderByAsc(column.getFieldData()!!.name)

  @Suppress("UNCHECKED_CAST")
  fun orderByAsc(vararg columns: KProperty1<T, *>): Children =
    func.orderByAsc(columns.map { column -> column.getFieldData()!!.name })

  fun orderByDesc(column: KProperty1<T, *>): Children = func.orderByDesc(column.getFieldData()!!.name).uncheckedCast()

  @Suppress("UNCHECKED_CAST")
  fun orderByDesc(vararg columns: KProperty1<T, *>): Children =
    func.orderByDesc(columns.map { column -> column.getFieldData()!!.name }).uncheckedCast()
}