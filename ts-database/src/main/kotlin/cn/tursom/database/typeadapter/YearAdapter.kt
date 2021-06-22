package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.year
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.time.Year
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object YearAdapter : TypeAdapter<Year> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, Year>): Column<Year>? {
    return if (field.returnType.jvmErasure == Year::class) {
      table.year(field)
    } else {
      null
    }
  }

  override fun toString() = "YearAdapter"
}