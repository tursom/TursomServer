package cn.tursom.database.wrapper

import cn.tursom.database.SqlUtils
import java.io.Serializable
import kotlin.reflect.KProperty

/**
 * @author miemie
 * @since 2018-12-12
 */
interface Query<T, Children> : Serializable {
  /**
   * 查询条件 SQL 片段
   */
  val sqlSelect: String

  /**
   * 设置查询字段
   *
   * @param columns 字段数组
   * @return children
   */
  fun select(columns: Collection<String>): Children
  fun select(vararg columns: String): Children = select(columns.asList())
  fun select(columns: Iterable<KProperty<T>>): Children = select(columns.map { SqlUtils { it.selectField } })
  fun select(vararg columns: KProperty<T>): Children = select(columns.asList())
}