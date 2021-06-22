package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.jdbcDate
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.sql.Date
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object DateAdapter : TypeAdapter<Date> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Date>): Column<Date>? {
    return if (field.returnType.jvmErasure == Date::class) {
      table.jdbcDate(field)
    } else {
      null
    }
  }

  override fun toString() = "DateAdapter"
}