package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.double
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object DoubleAdapter : TypeAdapter<Double> {
    override fun register(table: BaseTable<Any>, field: KProperty1<Any, Double>): Column<Double>? {
        return if (field.returnType.jvmErasure == Double::class) {
            table.double(field)
        } else {
            null
        }
    }

    override fun toString() = "DoubleAdapter"
}