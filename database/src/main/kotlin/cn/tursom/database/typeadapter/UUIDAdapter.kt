package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.uuid
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object UUIDAdapter : TypeAdapter<UUID> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, UUID>): Column<UUID>? {
    return if (field.returnType.jvmErasure == UUID::class) {
      table.uuid(field)
    } else {
      null
    }
  }
}