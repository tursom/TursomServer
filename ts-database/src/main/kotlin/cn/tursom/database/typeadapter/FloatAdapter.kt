package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.float
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object FloatAdapter : TypeAdapter<Float> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Float>): Column<Float>? {
    return if (field.returnType.jvmErasure == Float::class) {
      table.float(field)
    } else {
      null
    }
  }

  override fun toString() = "FloatAdapter"
}