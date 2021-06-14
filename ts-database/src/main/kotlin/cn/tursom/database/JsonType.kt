package cn.tursom.database

import cn.tursom.core.Utils
import com.google.gson.Gson
import me.liuwj.ktorm.schema.SqlType
import java.lang.reflect.Type
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

@Suppress("MemberVisibilityCanBePrivate")
class JsonType<T : Any>(
  val clazz: Type,
  type: Int = Types.VARCHAR,
  val gson: Gson = Utils.gson,
) : SqlType<T>(type, "json") {
  constructor(clazz: Class<T>, type: Int = Types.VARCHAR) : this(clazz as Type, type)

  override fun doGetResult(rs: ResultSet, index: Int): T? {
    val result = rs.getString(index) ?: return null
    return gson.fromJson(result, clazz)
  }

  override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: T) {
    ps.setString(index, gson.toJson(parameter))
  }
}