package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.datetime
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.time.LocalDateTime
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object LocalDateTimeAdapter : TypeAdapter<LocalDateTime> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, LocalDateTime>): Column<LocalDateTime>? {
    return if (field.returnType.jvmErasure == LocalDateTime::class) {
      table.datetime(field)
    } else {
      null
    }
  }

  override fun toString() = "LocalDateTimeAdapter"
}