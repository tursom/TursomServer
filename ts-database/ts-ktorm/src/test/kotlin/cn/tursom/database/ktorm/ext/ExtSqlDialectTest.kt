package cn.tursom.database.ktorm.ext

import cn.tursom.database.ktorm.ext.DirectSqlExpression.Companion.sql
import org.junit.Test
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.schema.IntSqlType
import org.sqlite.SQLiteDataSource

internal class ExtSqlDialectTest {
  @Test
  fun createSqlFormatter() {
    val database = Database.connect(SQLiteDataSource().let {
      it.url = "jdbc:sqlite::memory:"
      it
    }, dialect = ExtSqlDialect())

    val formatter = database.dialect.createSqlFormatter(database, false, 2)
    formatter.visit(IntSqlType sql "select count(*) from user u where u.uid = subscribe.mid" eq 0)
    println(formatter.sql)
  }
}


data class User(var name: String, var age: Int)


fun main() {

}
