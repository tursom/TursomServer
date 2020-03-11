package cn.tursom.database

import cn.tursom.core.Parser
import java.sql.ResultSet
import java.sql.SQLException

open class SqlAdapter<T : Any>(private val clazz: Class<T>) : ArrayList<T>() {
  open fun adapt(resultSet: ResultSet) {
    clear() //清空已储存的数据
      val resultMap = ResultSetMap(resultSet)
      // 遍历ResultSet
      while (resultSet.next()) {
        try {
        add(Parser.parse(resultMap, clazz) ?: continue)
        } catch (e: SQLException) {
          // ignored
        } catch (e: IllegalStateException) {
          // ignored
        }
      }
  }
}