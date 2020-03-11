package cn.tursom.database

import javax.sql.DataSource

class MysqlSqlTemplate(
  private val dataSource: DataSource
) : SqlTemplate {

  override fun <T : Any> getHelper(clazz: Class<T>): MysqlSqlHelper<T> {
    TODO("Not yet implemented")
  }

  companion object {
    init {
      Class.forName("com.mysql.cj.jdbc.Driver");
    }
  }
}