package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.bytes
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object BytesAdapter : TypeAdapter<ByteArray> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, ByteArray>): Column<ByteArray>? {
    return if (field.returnType.jvmErasure == ByteArray::class) {
      table.bytes(field)
    } else {
      null
    }
  }

  override fun toString() = "BytesAdapter"
}