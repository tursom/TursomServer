package cn.tursom.database

import cn.tursom.database.wrapper.IUpdateWrapper
import cn.tursom.database.wrapper.Query
import cn.tursom.database.wrapper.Wrapper
import java.io.Serializable
import java.sql.PreparedStatement
import java.sql.Types
import java.util.*
import javax.sql.DataSource

class MysqlSqlHelper<T : Any>(
  val clazz: Class<T>,
  var dataSource: DataSource
) : SqlHelper<T> {
  private val table = SqlUtils { clazz.tableName }
  private val columns = clazz.declaredFields.map {
    SqlUtils { it.tableField } to it
  }
  private val insert = run {
    val sql = StringBuilder("INSERT INTO $table (")
    columns.forEach {
      sql.append(it.first)
      sql.append(',')
    }
    sql.deleteCharAt(sql.length - 1)
    sql.append(") VALUES ")
    sql.toString()
  }
  private val valuesTemplate = run {
    val sql = StringBuilder("(")
    repeat(columns.size) {
      sql.append("?,")
    }
    sql.deleteCharAt(sql.length - 1)
    sql.append(")")
    sql.toString()
  }
  private val insertOne = "$insert$valuesTemplate;"

  private val selects = clazz.declaredFields
    .map { SqlUtils { it.selectField } }
    .let {
      val sb = StringBuilder()
      it.forEach { field ->
        sb.append(field)
        sb.append(',')
      }
      sb.deleteCharAt(sb.lastIndex)
      sb.toString()
    }

  override fun save(entity: T): Boolean {
    val conn = dataSource.connection
    val statement = conn.prepareStatement(insertOne)
    columns.forEachIndexed { index, (_, field) ->
      setValue(statement, index, field.get(entity))
    }
    return statement.executeUpdate() != 0
  }

  override fun saveBatch(entityList: Collection<T>): Int {
    if (entityList.isEmpty()) {
      return 0
    }
    val sqlBuilder = StringBuilder(insert)
    repeat(entityList.size) {
      sqlBuilder.append(valuesTemplate)
      sqlBuilder.append(',')
    }
    sqlBuilder.deleteCharAt(sqlBuilder.lastIndex)
    sqlBuilder.append(';')
    val sql = sqlBuilder.toString()

    val conn = dataSource.connection
    val statement = conn.prepareStatement(sql)
    try {
      var index = 0
      entityList.forEach {
        columns.forEach { (_, field) ->
          setValue(statement, index, field.get(it))
          index++
        }
      }
      return statement.executeUpdate()
    } finally {
      statement.close()
      conn.close()
    }
  }

  //override fun saveOrUpdateBatch(entityList: Collection<T>, batchSize: Int): Boolean {
  //  TODO("Not yet implemented")
  //}

  override fun removeById(id: Serializable?): Boolean {
    TODO("Not yet implemented")
  }

  override fun removeByMap(columnMap: Map<String, Any?>): Boolean {
    TODO("Not yet implemented")
  }

  override fun remove(queryWrapper: Wrapper<T>): Boolean {
    TODO("Not yet implemented")
  }

  override fun removeByIds(idList: Collection<Serializable>): Boolean {
    TODO("Not yet implemented")
  }

  override fun updateById(entity: T): Boolean {
    TODO("Not yet implemented")
  }

  override fun update(entity: T?, updateWrapper: IUpdateWrapper<T>): Boolean {
    TODO("Not yet implemented")
  }

  override fun updateBatchById(entityList: Collection<T>, batchSize: Int): Boolean {
    TODO("Not yet implemented")
  }

  override fun saveOrUpdate(entity: T?): Boolean {
    TODO("Not yet implemented")
  }

  override fun getById(id: Serializable?): T? {
    TODO("Not yet implemented")
  }

  override fun listByIds(idList: Collection<Serializable>): Collection<T> {
    TODO("Not yet implemented")
  }

  override fun listByMap(columnMap: Map<String, Any?>): Collection<T> {
    TODO("Not yet implemented")
  }

  override fun getOne(queryWrapper: Wrapper<T>, throwEx: Boolean): T? {
    TODO("Not yet implemented")
  }

  override fun getMap(queryWrapper: Wrapper<T>): Map<String, Any?> {
    TODO("Not yet implemented")
  }

  override fun count(queryWrapper: Wrapper<T>): Int {
    TODO("Not yet implemented")
  }

  override fun list(queryWrapper: Wrapper<T>): List<T> {
    val select = if (queryWrapper !is Query<*, *>) {
      selects
    } else {
      val select = queryWrapper.sqlSelect
      if (select.isEmpty()) {
        selects
      } else {
        select
      }
    }

    val (where, whereParams) = queryWrapper.where
    val sql = "SELECT $select FROM $table WHERE (${where});"
    dataSource.connection.use { conn ->
      conn.prepareStatement(sql).use { statement ->
        var index = 0
        whereParams.forEach {
          setValue(statement, index, it)
          index++
        }
        val query = statement.executeQuery()
        val adapter = SqlAdapter(clazz)
        query.use { adapter.adapt(query) }
        return adapter
      }
    }
  }

  override fun getBaseMapper(): Mapper<T> {
    TODO("Not yet implemented")
  }

  companion object {
    fun setValue(statement: PreparedStatement, index: Int, value: Any?) {
      when (value) {
        null -> statement.setNull(index + 1, Types.NULL)
        is Date -> statement.setDate(index + 1, java.sql.Date(value.time))
        else -> statement.setObject(index + 1, value)
      }
    }
  }
}