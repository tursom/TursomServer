package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.long
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

class LongAdapter : TypeAdapter<Long> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Long>): Column<Long>? {
    return if (field.returnType.jvmErasure == Long::class) {
      table.long(field)
    } else {
      null
    }
  }
}