package cn.tursom.database.typeadapter

import cn.tursom.core.cast
import cn.tursom.database.SqlUtils
import cn.tursom.database.TypeAdapter
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
      table.registerColumn(SqlUtils { field.tableField }, EnumSqlType<EnumType>(kClass.java.cast())).cast()
    } else {
      null
    }
  }

  override fun toString() = "EnumAdapter"

}