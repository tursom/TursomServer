package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.varchar
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object StringAdapter : TypeAdapter<String> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, String>): Column<String>? {
    return if (field.returnType.jvmErasure == String::class) {
      table.varchar(field)
    } else {
      null
    }
  }

  override fun toString() = "StringAdapter"
}