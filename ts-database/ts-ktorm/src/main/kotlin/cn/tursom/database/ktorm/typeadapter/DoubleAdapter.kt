package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.double
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object DoubleAdapter : TypeAdapter<Double> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Double>): Column<Double>? {
    return if (field.returnType.jvmErasure == Double::class) {
      table.double(field)
    } else {
      null
    }
  }

  override fun toString() = "DoubleAdapter"
}