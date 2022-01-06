package cn.tursom.database.ktorm

import org.ktorm.dsl.Query
import org.ktorm.dsl.map

inline fun <reified R : Any> Query.toList(): List<R> {
  val table = AutoTable[R::class]
  return map {
    table.createEntity(it)
  }
}
