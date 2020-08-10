package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.boolean
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
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
}