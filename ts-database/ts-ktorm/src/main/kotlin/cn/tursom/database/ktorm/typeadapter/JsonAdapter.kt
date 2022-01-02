package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.annotations.Json
import cn.tursom.database.ktorm.json
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

object JsonAdapter : TypeAdapter<Int> {
  override val level: Int get() = -16

  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Int>): Column<Int>? {
    return if (field.findAnnotation<Json>() != null) {
      table.json(field)
    } else {
      null
    }
  }

  override fun toString() = "JsonAdapter"
}