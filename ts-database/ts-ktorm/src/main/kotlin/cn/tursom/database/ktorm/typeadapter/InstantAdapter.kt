package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.timestamp
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.time.Instant
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object InstantAdapter : TypeAdapter<Instant> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Instant>): Column<Instant>? {
    return if (field.returnType.jvmErasure == Instant::class) {
      table.timestamp(field)
    } else {
      null
    }
  }

  override fun toString() = "InstantAdapter"
}