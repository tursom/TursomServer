package cn.tursom.database.typeadapter

import cn.tursom.database.TypeAdapter
import cn.tursom.database.decimal
import me.liuwj.ktorm.schema.BaseTable
import me.liuwj.ktorm.schema.Column
import java.math.BigDecimal
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

object IntAdapterAdapter : TypeAdapter<BigDecimal> {
  override fun register(table: BaseTable<Any>, field: KProperty1<Any, BigDecimal>): Column<BigDecimal>? {
    return if (field.returnType.jvmErasure == BigDecimal::class) {
      table.decimal(field)
    } else {
      null
    }
  }
}