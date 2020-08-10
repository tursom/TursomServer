package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.time
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import java.time.LocalTime
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

class LocalTimeAdapter : TypeAdapter<LocalTime> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, LocalTime>): Column<LocalTime>? {
    return if (field.returnType.jvmErasure == LocalTime::class) {
      table.time(field)
    } else {
      null
    }
  }
}