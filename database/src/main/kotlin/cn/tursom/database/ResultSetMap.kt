package cn.tursom.database

import java.sql.ResultSet

class ResultSetMap(
  val resultSet: ResultSet
) : Map<String, Any?> {
  override val entries: Set<Map.Entry<String, Any?>> get() = TODO("Not yet implemented")
  override val keys: Set<String> get() = TODO("Not yet implemented")
  override val size: Int get() = resultSet.fetchSize
  override val values: Collection<Any?> get() = TODO("Not yet implemented")
  override fun containsKey(key: String): Boolean = TODO("Not yet implemented")
  override fun containsValue(value: Any?): Boolean = TODO("Not yet implemented")
  override fun isEmpty(): Boolean = TODO("Not yet implemented")

  override fun get(key: String): Any? = resultSet.getObject(key)
}