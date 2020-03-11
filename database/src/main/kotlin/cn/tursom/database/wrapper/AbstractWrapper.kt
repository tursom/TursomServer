package cn.tursom.database.wrapper

import java.util.*


abstract class AbstractWrapper<T, Children : AbstractWrapper<T, Children>> : Wrapper<T>,
  Compare<T, Children>,
  Join<T, Children>,
  Func<T, Children> {
  @Suppress("UNCHECKED_CAST", "LeakingThis")
  private val typedThis = this as Children

  override val table: String get() = TODO()
  override val where: Pair<String, Collection<Any?>>
    get() {
      if (whereBuilder.endsWith(") OR (")) {
        repeat(") OR (".length) {
          whereBuilder.deleteCharAt(whereBuilder.length - 1)
        }
      }
      return whereBuilder.toString() to whereParams
    }

  protected val whereBuilder = StringBuilder()
  protected val whereParams = LinkedList<Any?>()

  protected fun newWhere() = when {
    whereBuilder.endsWith(") OR (") -> whereBuilder
    whereBuilder.isNotEmpty() -> whereBuilder.append(" AND ")
    else -> whereBuilder.append("(")
  }

  override fun allEq(params: Map<String, Any?>): Children {
    params.forEach { (t, u) ->
      t eq u
    }
    return typedThis
  }

  override fun String.eq(value: Any?): Children {
    newWhere()
    if (value != null) {
      whereBuilder.append("$this=?")
      whereParams.add(value)
    } else {
      whereBuilder.append("$this IS NULL")
    }
    return typedThis
  }

  override fun String.ne(value: Any?): Children {
    newWhere()
    if (value != null) {
      whereBuilder.append("$this <> ?")
      whereParams.add(value)
    } else {
      whereBuilder.append("$this IS NOT NULL")
    }
    return typedThis
  }

  override fun String.gt(value: Any): Children {
    newWhere()
    whereBuilder.append("$this > ?")
    whereParams.add(value)
    return typedThis
  }

  override fun String.ge(value: Any): Children {
    newWhere()
    whereBuilder.append("$this >= ?")
    whereParams.add(value)
    return typedThis
  }

  override fun String.le(value: Any): Children {
    newWhere()
    whereBuilder.append("$this < ?")
    whereParams.add(value)
    return typedThis
  }

  override fun String.lt(value: Any): Children {
    newWhere()
    whereBuilder.append("$this <= ?")
    whereParams.add(value)
    return typedThis
  }

  override fun String.between(val1: Any, val2: Any): Children {
    newWhere()
    whereBuilder.append("$this BETWEEN ? AND ?")
    whereParams.add(val1)
    whereParams.add(val2)
    return typedThis
  }

  override fun String.notBetween(val1: Any, val2: Any): Children {
    newWhere()
    whereBuilder.append("$this NOT BETWEEN ? AND ?")
    whereParams.add(val1)
    whereParams.add(val2)
    return typedThis
  }

  override fun String.like(value: Any): Children {
    newWhere()
    whereBuilder.append("$this LIKE ?")
    whereParams.add(value)
    return typedThis
  }

  override fun String.notLike(value: Any): Children {
    newWhere()
    whereBuilder.append("$this NOT LIKE ?")
    whereParams.add(value)
    return typedThis
  }

  override fun String.likeLeft(value: Any): Children {
    newWhere()
    whereBuilder.append("$this LIKE ?")
    whereParams.add("${value}%")
    return typedThis
  }

  override fun String.likeRight(value: Any): Children {
    newWhere()
    whereBuilder.append("$this LIKE ?")
    whereParams.add("%${value}")
    return typedThis
  }

  override fun String.isNull(): Children {
    newWhere()
    whereBuilder.append("$this IS NULL")
    return typedThis
  }

  override fun String.isNotNull(): Children {
    newWhere()
    whereBuilder.append("$this IS NOT NULL")
    return typedThis
  }

  override fun String.`in`(coll: Collection<Any?>): Children {
    newWhere()
    if (coll.isEmpty()) return typedThis
    whereBuilder.append("$this IN (")
    coll.forEach {
      whereBuilder.append("?,")
      whereParams.add(it)
    }
    whereBuilder.deleteCharAt(whereBuilder.length - 1)
    whereBuilder.append(")")
    return typedThis
  }

  override fun String.notIn(coll: Collection<Any?>): Children {
    newWhere()
    if (coll.isEmpty()) return typedThis
    whereBuilder.append("$this NOT IN (")
    coll.forEach {
      whereBuilder.append("?,")
      whereParams.add(it)
    }
    whereBuilder.deleteCharAt(whereBuilder.length - 1)
    whereBuilder.append(")")
    return typedThis
  }

  override fun String.inSql(inValue: String): Children {
    newWhere()
    whereBuilder.append("$this IN $inValue")
    return typedThis
  }

  override fun String.notInSql(inValue: String): Children {
    newWhere()
    whereBuilder.append("$this NOT IN $inValue")
    return typedThis
  }

  protected val groupByBuilder = StringBuilder()
  override val groupBy: String?
    get() = if (groupByBuilder.isEmpty()) null
    else groupByBuilder.toString()

  override fun groupBy(columns: Collection<String>): Children {
    if (columns.isEmpty()) return typedThis
    if (groupByBuilder.isEmpty()) groupByBuilder.append("GROUP BY ")
    else groupByBuilder.append(',')
    columns.forEach {
      groupByBuilder.append(it)
      groupByBuilder.append(',')
    }
    groupByBuilder.deleteCharAt(groupByBuilder.length - 1)
    return typedThis
  }

  protected val orderByBuilder = StringBuilder()
  override val orderBy: String?
    get() = if (orderByBuilder.isEmpty()) null
    else orderByBuilder.toString()

  override fun orderByAsc(columns: Collection<String>): Children {
    if (columns.isEmpty()) return typedThis
    if (orderByBuilder.isEmpty()) orderByBuilder.append("ORDER BY ")
    else orderByBuilder.append(',')
    columns.forEach {
      orderByBuilder.append(it)
      orderByBuilder.append(" ASC,")
    }
    orderByBuilder.deleteCharAt(orderByBuilder.length - 1)
    return typedThis
  }

  override fun orderByDesc(columns: Collection<String>): Children {
    if (columns.isEmpty()) return typedThis
    if (orderByBuilder.isEmpty()) orderByBuilder.append("ORDER BY ")
    else orderByBuilder.append(',')
    columns.forEach {
      orderByBuilder.append(it)
      orderByBuilder.append(" DESC,")
    }
    orderByBuilder.deleteCharAt(orderByBuilder.length - 1)
    return typedThis
  }

  override fun orderBy(isAsc: Boolean, columns: Collection<String>): Children {
    if (columns.isEmpty()) return typedThis
    return if (isAsc) {
      orderByAsc(columns)
    } else {
      orderByDesc(columns)
    }
  }

  protected val havingBuilder = StringBuilder()
  override val having: String?
    get() = if (havingBuilder.isEmpty()) null
    else havingBuilder.toString()
  override val havingParams = LinkedList<Any?>()
  override fun having(sqlHaving: String, vararg params: Any?): Children {
    havingBuilder.append(sqlHaving)
    havingParams.addAll(params)
    return typedThis
  }

  override fun or(): Children {
    whereBuilder.append(") OR (")
    return typedThis
  }

  override var last: String = ""

  override fun last(lastSql: String): Children {
    last = lastSql
    return typedThis
  }
}