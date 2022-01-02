package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.float
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
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