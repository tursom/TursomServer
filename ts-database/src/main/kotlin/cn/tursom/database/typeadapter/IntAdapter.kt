package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.int
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object IntAdapter : TypeAdapter<Int> {
    override fun register(table: BaseTable<Any>, field: KProperty1<Any, Int>): Column<Int>? {
        return if (field.returnType.jvmErasure == Int::class) {
            table.int(field)
        } else {
            null
        }
    }

    override fun toString() = "IntAdapter"
}