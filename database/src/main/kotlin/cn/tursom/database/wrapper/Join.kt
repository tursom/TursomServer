package cn.tursom.database.wrapper

import java.io.Serializable

interface Join<T, Children> : Serializable {
  fun or(): Children
  //fun apply(applySql: String, vararg value: Any?): Children
  var last: String
  fun last(lastSql: String): Children
  //fun comment(comment: String): Children
  //fun exists(existsSql: String): Children
  //fun notExists(notExistsSql: String): Children
}