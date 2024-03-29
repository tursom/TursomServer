package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.long
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object LongAdapter : TypeAdapter<Long> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Long>): Column<Long>? {
    return if (field.returnType.jvmErasure == Long::class) {
      table.long(field)
    } else {
      null
    }
  }

  override fun toString() = "LongAdapter"
}