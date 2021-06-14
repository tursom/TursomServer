package cn.tursom.database.typeadapter

import cn.tursom.core.uncheckedCast
import cn.tursom.database.TypeAdapter
import cn.tursom.database.simpTableField
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import me.liuwj.ktorm.schema.EnumSqlType
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

object EnumAdapter : TypeAdapter<EnumAdapter.EnumType> {
  enum class EnumType

  override fun register(table: BaseTable<Any>, field: KProperty1<Any, EnumType>): Column<EnumType>? {
    val kClass = field.returnType.jvmErasure
    return if (kClass.isSubclassOf(Enum::class)) {
      table.registerColumn(field.simpTableField, EnumSqlType<EnumType>(kClass.java.uncheckedCast()))
        .uncheckedCast()
    } else {
      null
    }
  }

  override fun toString() = "EnumAdapter"

}