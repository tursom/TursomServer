package cn.tursom.database.wrapper

import java.io.Serializable
import kotlin.reflect.KProperty1

interface Func<Children, T> : Serializable {
  fun isNull(column: KProperty1<T, *>): Children {
    return this.isNull(true, column)
  }

  fun isNull(condition: Boolean, column: KProperty1<T,*>): Children
  fun isNotNull(column: KProperty1<T,*>): Children {
    return this.isNotNull(true, column)
  }

  fun isNotNull(condition: Boolean, column: KProperty1<T,*>): Children
  fun `in`(column: KProperty1<T,*>, coll: Collection<*>?): Children {
    return this.`in`(true, column, coll)
  }

  fun `in`(condition: Boolean, column: KProperty1<T,*>, coll: Collection<*>?): Children
  fun `in`(column: KProperty1<T,*>, vararg values: Any?): Children {
    return this.`in`(true, column, *values)
  }

  fun `in`(condition: Boolean, column: KProperty1<T,*>, vararg values: Any?): Children {
    return this.`in`(condition, column, values.asList())
  }

  fun notIn(column: KProperty1<T,*>, coll: Collection<*>?): Children {
    return this.notIn(true, column, coll)
  }

  fun notIn(condition: Boolean, column: KProperty1<T,*>, coll: Collection<*>?): Children
  fun notIn(column: KProperty1<T,*>, vararg value: Any?): Children {
    return this.notIn(true, column, *value)
  }

  fun notIn(condition: Boolean, column: KProperty1<T,*>, vararg values: Any?): Children {
    return this.notIn(condition, column, values.asList())
  }

  fun inSql(column: KProperty1<T,*>, inValue: String?): Children {
    return this.inSql(true, column, inValue)
  }

  fun inSql(condition: Boolean, column: KProperty1<T,*>, inValue: String?): Children
  fun notInSql(column: KProperty1<T,*>, inValue: String?): Children {
    return this.notInSql(true, column, inValue)
  }

  fun notInSql(condition: Boolean, column: KProperty1<T,*>, inValue: String?): Children
  fun groupBy(column: KProperty1<T,*>): Children {
    return this.groupBy(true, column)
  }

  fun groupBy(vararg columns: KProperty1<T,*>): Children {
    return this.groupBy(true, *columns)
  }

  fun groupBy(condition: Boolean, vararg columns: KProperty1<T,*>): Children
  fun orderByAsc(column: KProperty1<T,*>): Children {
    return this.orderByAsc(true, column)
  }

  fun orderByAsc(vararg columns: KProperty1<T,*>): Children {
    return this.orderByAsc(true, *columns)
  }

  fun orderByAsc(condition: Boolean, vararg columns: KProperty1<T,*>): Children {
    return orderBy(condition, true, *columns)
  }

  fun orderByDesc(column: KProperty1<T,*>): Children {
    return this.orderByDesc(true, column)
  }

  fun orderByDesc(vararg columns: KProperty1<T,*>): Children {
    return this.orderByDesc(true, *columns)
  }

  fun orderByDesc(condition: Boolean, vararg columns: KProperty1<T,*>): Children {
    return orderBy(condition, false, *columns)
  }

  fun orderBy(condition: Boolean, isAsc: Boolean, vararg columns: KProperty1<T,*>): Children
  fun having(sqlHaving: String?, vararg params: Any?): Children {
    return this.having(true, sqlHaving, *params)
  }

  fun having(condition: Boolean, sqlHaving: String?, vararg params: Any?): Children
}