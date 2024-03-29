package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.jdbcTime
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.sql.Time
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object TimeAdapter : TypeAdapter<Time> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Time>): Column<Time>? {
    return if (field.returnType.jvmErasure == Time::class) {
      table.jdbcTime(field)
    } else {
      null
    }
  }

  override fun toString() = "TimeAdapter"
}