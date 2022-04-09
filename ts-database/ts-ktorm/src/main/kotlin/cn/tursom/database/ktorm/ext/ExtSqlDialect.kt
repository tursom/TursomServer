package cn.tursom.database.ktorm.ext

import cn.tursom.core.allFieldsSequence
import cn.tursom.core.reflect.InstantAllocator
import cn.tursom.proxy.Proxy
import org.ktorm.database.Database
import org.ktorm.database.SqlDialect
import org.ktorm.database.detectDialectImplementation
import org.ktorm.expression.SqlFormatter

class ExtSqlDialect(
  private val sqlDialect: SqlDialect = detectDialectImplementation(),
) : SqlDialect by sqlDialect {
  override fun createSqlFormatter(database: Database, beautifySql: Boolean, indentSize: Int): SqlFormatter {
    val formatter = sqlDialect.createSqlFormatter(database, beautifySql, indentSize)
    val (proxyFormatter, container) = Proxy.get(formatter.javaClass) { InstantAllocator(it) }
    run {
      val extSqlFormatter = ExtSqlFormatter(formatter)
      extSqlFormatter.registerVisitor(DirectSqlExpression.visitor)
      container.addProxy(extSqlFormatter)
    }
    formatter.javaClass.allFieldsSequence.forEach { field ->
      field.isAccessible = true
      field.set(proxyFormatter, field.get(formatter))
    }
    return proxyFormatter
  }
}