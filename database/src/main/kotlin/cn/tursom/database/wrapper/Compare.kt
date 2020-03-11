package cn.tursom.database.wrapper

import cn.tursom.database.SqlUtils
import java.io.Serializable
import kotlin.reflect.KProperty1

interface Compare<T, Children> : Serializable {
  /**
   * map 所有属性等于 =
   *
   * @param params      map 类型的参数, key 是字段名, value 是字段值
   * @return children
   */
  fun allEq(params: Map<String, Any?>): Children
  fun Map<KProperty1<T, *>, Any?>.eq(): Children = allEq(SqlUtils { mapKeys { it.key.tableField } })

  infix fun String.eq(value: Any?): Children
  infix fun <V> KProperty1<T, V>.eq(value: V): Children = SqlUtils { tableField }.eq(value)

  infix fun String.ne(value: Any?): Children
  infix fun <V> KProperty1<T, V>.ne(value: V): Children = SqlUtils { tableField }.ne(value)

  infix fun String.gt(value: Any): Children
  infix fun <V : Any> KProperty1<T, V>.gt(value: V): Children = SqlUtils { tableField }.gt(value)

  infix fun String.ge(value: Any): Children
  infix fun <V : Any> KProperty1<T, V>.ge(value: V): Children = SqlUtils { tableField }.ge(value)

  infix fun String.le(value: Any): Children
  infix fun <V : Any> KProperty1<T, V>.le(value: V): Children = SqlUtils { tableField }.le(value)

  infix fun String.lt(value: Any): Children
  infix fun <V : Any> KProperty1<T, V>.lt(value: V): Children = SqlUtils { tableField }.lt(value)

  fun String.between(val1: Any, val2: Any): Children
  fun <V : Any> KProperty1<T, V>.between(val1: V, val2: V): Children = SqlUtils { tableField }.between(val1, val2)

  fun String.notBetween(val1: Any, val2: Any): Children
  fun <V : Any> KProperty1<T, V>.notBetween(val1: V, val2: V): Children = SqlUtils { tableField }.notBetween(val1, val2)

  infix fun String.like(value: Any): Children
  infix fun <V : Any> KProperty1<T, V>.like(value: V): Children = SqlUtils { tableField }.like(value)

  infix fun String.notLike(value: Any): Children
  infix fun <V : Any> KProperty1<T, V>.notLike(value: V): Children = SqlUtils { tableField }.notLike(value)

  infix fun String.likeLeft(value: Any): Children
  infix fun <V : Any> KProperty1<T, V>.likeLeft(value: V): Children = SqlUtils { tableField }.likeLeft(value)

  infix fun String.likeRight(value: Any): Children
  infix fun <V : Any> KProperty1<T, V>.likeRight(value: V): Children = SqlUtils { tableField }.likeRight(value)

}