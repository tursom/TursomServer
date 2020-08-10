package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.monthDay
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
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
}