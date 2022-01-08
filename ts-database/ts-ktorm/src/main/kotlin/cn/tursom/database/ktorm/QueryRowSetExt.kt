package cn.tursom.database.ktorm

import org.ktorm.dsl.QueryRowSet
import kotlin.reflect.KProperty1

inline operator fun <reified T : Any, C : Any> QueryRowSet.get(column: KProperty1<in T, C?>) =
  get(AutoTable[T::class.java][column])
