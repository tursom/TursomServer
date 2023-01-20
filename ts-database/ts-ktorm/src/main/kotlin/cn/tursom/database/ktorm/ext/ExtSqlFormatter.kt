package cn.tursom.database.ktorm.ext

import cn.tursom.proxy.function.ProxyMethod
import cn.tursom.reflect.asm.ReflectAsmKtField
import org.ktorm.expression.SqlExpression
import org.ktorm.expression.SqlFormatter

class ExtSqlFormatter(
  private val prevFormatter: SqlFormatter,
) : ProxyMethod {
  companion object {
    private val builderField = ReflectAsmKtField.get<SqlFormatter, StringBuilder>("_builder")
  }

  private val visitorList = ArrayList<(expr: SqlExpression, builder: StringBuilder) -> Boolean>()

  fun registerVisitor(visitor: (expr: SqlExpression, builder: StringBuilder) -> Boolean) {
    visitorList.add(visitor)
  }

  fun visitUnknown(expr: SqlExpression): SqlExpression {
    val builder = builderField[prevFormatter]
    visitorList.forEach {
      if (it(expr, builder)) {
        return expr
      }
    }
    return expr
  }
}