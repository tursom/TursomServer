package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.boolean
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object BooleanAdapter : TypeAdapter<Boolean> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Boolean>): Column<Boolean>? {
    return if (field.returnType.jvmErasure == Boolean::class) {
      table.boolean(field)
    } else {
      null
    }
  }

  override fun toString() = "BooleanAdapter"
}