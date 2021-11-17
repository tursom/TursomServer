package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.yearMonth
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.time.YearMonth
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object YearMonthAdapter : TypeAdapter<YearMonth> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, YearMonth>): Column<YearMonth>? {
    return if (field.returnType.jvmErasure == YearMonth::class) {
      table.yearMonth(field)
    } else {
      null
    }
  }

  override fun toString() = "YearMonthAdapter"
}