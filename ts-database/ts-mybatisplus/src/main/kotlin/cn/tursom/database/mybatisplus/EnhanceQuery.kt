package cn.tursom.database.mybatisplus

import cn.tursom.core.uncheckedCast
import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.conditions.query.Query
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

@Suppress("unused")
@MybatisPlusEnhanceDslMaker
interface EnhanceQuery<T, out Children : Wrapper<T>> : EnhanceEntityClassEnhance<T> {
  val query: Query<out Children, T, out Any> get() = uncheckedCast()

  fun select(
    columns: Collection<String>,
  ): Children = query.select(enhanceEntityClass) {
    it.column in columns
  }

  fun select(
    columns: Sequence<String>,
  ): Children = select(columns.toSet())

  /**
   * QueryWrapper<T>().select(T::fieldName, value)
   */
  fun select(
    vararg columns: KProperty1<T, *>,
  ): Children = select(columns = columns as Array<out KProperty<*>>)

  fun select(
    vararg columns: KProperty<*>,
  ): Children = select(columns.mapNotNull {
    it.getFieldData()?.field?.name
  }.toSet())

  fun fullSelect(
    vararg columns: KProperty<*>,
  ): Children = selectMethod(uncheckedCast(), columns.mapNotNull {
    it.getFieldData()?.selectionName
  }.toTypedArray()).uncheckedCast()

  fun joinSelect(
    vararg columns: KProperty<*>,
  ): Children = fullSelect(columns = columns)
}