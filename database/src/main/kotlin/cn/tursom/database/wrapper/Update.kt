package cn.tursom.database.wrapper

import cn.tursom.database.SqlUtils
import java.io.Serializable
import kotlin.reflect.KProperty1

interface Update<T, Children> : Serializable {
  fun setSql(sql: String, values: Collection<Any?>): Children
  fun setSql(sql: String, vararg values: Any?): Children = setSql(sql, values.asList())

  infix fun String.set(value: T): Children
  infix fun <R> KProperty1<T, R>.set(value: R): Children = set(SqlUtils { tableField })
}

