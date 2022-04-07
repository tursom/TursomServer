package cn.tursom.database.ktorm.ext

import org.ktorm.expression.ScalarExpression
import org.ktorm.expression.SqlExpression
import org.ktorm.schema.SqlType

class DirectSqlExpression<T : Any>(
  val sql: String,
  override val sqlType: SqlType<T>,
  override val isLeafNode: Boolean = false,
  override val extraProperties: Map<String, Any> = emptyMap(),
) : ScalarExpression<T>() {

  companion object {
    val visitor = { expr: SqlExpression, builder: StringBuilder ->
      if (expr is DirectSqlExpression<*>) {
        builder.append(expr.sql)
        true
      } else {
        false
      }
    }

    infix fun <T : Any> SqlType<T>.sql(sql: String) = DirectSqlExpression(sql, this)
  }
}