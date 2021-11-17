package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.int
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object IntAdapter : TypeAdapter<Int> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Int>): Column<Int>? {
    return if (field.returnType.jvmErasure == Int::class) {
      table.int(field)
    } else {
      null
    }
  }

  override fun toString() = "IntAdapter"
}