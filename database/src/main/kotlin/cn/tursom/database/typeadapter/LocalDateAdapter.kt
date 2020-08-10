package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.date
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
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