package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.jdbcTime
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
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
}