package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.jdbcTimestamp
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import java.sql.Timestamp
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

class TimestampAdapter : TypeAdapter<Timestamp> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Timestamp>): Column<Timestamp>? {
    return if (field.returnType.jvmErasure == Timestamp::class) {
      table.jdbcTimestamp(field)
    } else {
      null
    }
  }
}