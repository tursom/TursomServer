package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.date
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.time.LocalDate
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object LocalDateAdapter : TypeAdapter<LocalDate> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, LocalDate>): Column<LocalDate>? {
    return if (field.returnType.jvmErasure == LocalDate::class) {
      table.date(field)
    } else {
      null
    }
  }

  override fun toString() = "LocalDateAdapter"
}