package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.datetime
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import java.time.LocalDateTime
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, LocalDateTime>): Column<LocalDateTime>? {
    return if (field.returnType.jvmErasure == LocalDateTime::class) {
      table.datetime(field)
    } else {
      null
    }
  }
}