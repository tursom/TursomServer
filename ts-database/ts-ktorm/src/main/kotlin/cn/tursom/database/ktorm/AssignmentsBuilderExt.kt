package cn.tursom.database.ktorm

import org.ktorm.dsl.AssignmentsBuilder
import org.ktorm.schema.ColumnDeclaring
import kotlin.reflect.KProperty1

inline fun <reified T : Any, C : Any> AssignmentsBuilder.set(
  column: KProperty1<T, C?>,
  value: C?,
) {
  set(AutoTable[T::class.java][column], value)
}

inline fun <reified T : Any, C : Any> AssignmentsBuilder.set(
  column: KProperty1<T, C?>,
  expr: ColumnDeclaring<C>,
) {
  set(AutoTable[T::class.java][column], expr)
}
