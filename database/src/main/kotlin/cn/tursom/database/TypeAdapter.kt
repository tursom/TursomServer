package cn.tursom.database

import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import kotlin.reflect.KProperty1

interface TypeAdapter<T : Any> {
  val level: Int get() = 0
  fun register(
    table: BaseTable<Any>,
    field: KProperty1<Any, T>
  ): Column<T>?
}