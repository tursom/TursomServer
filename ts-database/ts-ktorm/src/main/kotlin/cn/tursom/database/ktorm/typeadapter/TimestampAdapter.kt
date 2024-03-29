package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.jdbcTimestamp
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.sql.Timestamp
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object TimestampAdapter : TypeAdapter<Timestamp> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Timestamp>): Column<Timestamp>? {
    return if (field.returnType.jvmErasure == Timestamp::class) {
      table.jdbcTimestamp(field)
    } else {
      null
    }
  }

  override fun toString() = "TimestampAdapter"
}