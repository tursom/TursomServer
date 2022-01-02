package cn.tursom.database.ktorm.typeadapter

import cn.tursom.core.uncheckedCast
import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.simpTableField
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.EnumSqlType
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