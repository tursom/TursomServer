package cn.tursom.database.ktorm.typeadapter

import cn.tursom.database.ktorm.TypeAdapter
import cn.tursom.database.ktorm.monthDay
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.time.MonthDay
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object MonthDayAdapter : TypeAdapter<MonthDay> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, MonthDay>): Column<MonthDay>? {
    return if (field.returnType.jvmErasure == MonthDay::class) {
      table.monthDay(field)
    } else {
      null
    }
  }

  override fun toString() = "MonthDayAdapter"
}