package cn.tursom.database.ktorm

import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import kotlin.reflect.KProperty1

/**
 * 将属性转化为表列的适配器
 */
interface TypeAdapter<T : Any> {
  /**
   * 该适配器的优先级，优先级越高则越优先匹配
   * 同等优先级先注册者先匹配
   */
  val level: Int get() = 0

  /**
   * 注册列
   * 如果失败则返回null
   */
  fun register(
    table: BaseTable<Any>,
    field: KProperty1<Any, T>,
  ): Column<T>?
}