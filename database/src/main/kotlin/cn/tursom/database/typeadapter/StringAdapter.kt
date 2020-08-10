package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.varchar
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

class StringAdapter : TypeAdapter<String> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, String>): Column<String>? {
    return if (field.returnType.jvmErasure == String::class) {
      table.varchar(field)
    } else {
      null
    }
  }
}