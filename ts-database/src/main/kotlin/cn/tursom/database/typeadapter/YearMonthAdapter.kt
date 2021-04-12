package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.yearMonth
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
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