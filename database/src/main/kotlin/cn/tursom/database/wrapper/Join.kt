package cn.tursom.database.wrapper

import java.io.Serializable

interface Join<Children> : Serializable {
  fun or(condition: Boolean = true): Children

  fun apply(applySql: String?, vararg value: Any?): Children {
    return this.apply(true, applySql, *value)
  }

  fun apply(condition: Boolean, applySql: String?, vararg value: Any?): Children
  fun last(lastSql: String?): Children {
    return this.last(true, lastSql)
  }

  fun last(condition: Boolean, lastSql: String?): Children
  fun comment(comment: String?): Children {
    return this.comment(true, comment)
  }

  fun comment(condition: Boolean, comment: String?): Children
  fun exists(existsSql: String?): Children {
    return this.exists(true, existsSql)
  }

  fun exists(condition: Boolean, existsSql: String?): Children
  fun notExists(notExistsSql: String?): Children {
    return this.notExists(true, notExistsSql)
  }

  fun notExists(condition: Boolean, notExistsSql: String?): Children
}