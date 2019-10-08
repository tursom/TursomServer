package cn.tursom.database.sqlite

import cn.tursom.database.SqlUtils.fieldName
import cn.tursom.database.SqlUtils.fieldValue
import cn.tursom.database.SqlUtils.isSqlField
import cn.tursom.database.SqlUtils.ignored
import cn.tursom.database.SqlUtils.valueStr
import cn.tursom.database.SqlUtils.fieldStr
import cn.tursom.database.SqlUtils.tableName
import cn.tursom.database.SqlUtils.getAnnotation
import cn.tursom.database.*
import cn.tursom.database.annotation.*
import cn.tursom.database.clauses.Clause
import cn.tursom.core.simplifyPath
import org.sqlite.SQLiteException
import java.io.File
import java.lang.reflect.Field
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


/**
 * MySQLHelper，SQLite辅助使用类
 * 实现创建表格、查询、插入和更新功能
 */

@Suppress("SqlDialectInspection", "SqlNoDataSourceInspection")
open class SQLiteHelper
/**
 * 创建名为 base.db 的数据库连接
 */
(base: String) : SqlHelper {
	private val connection: Connection
	private val path = File(base).absolutePath.simplifyPath()

	init {
		synchronized(connectionMap) {
			connection = connectionMap[path] ?: {
				val connection = DriverManager.getConnection("jdbc:sqlite:$base") ?: throw CantConnectDataBase()
				connectionMap[path] = connection
				connection.autoCommit = false
				// 实现 REGEXP 函数
				org.sqlite.Function.create(connection, "REGEXP", regexp)
				connection
			}()
			connectionCount[path] = connectionCount[path] ?: 0 + 1
		}
	}

	override val closed: Boolean get() = connection.isClosed

	override fun equals(other: Any?): Boolean =
		if (other is SQLiteHelper) {
			connection == other.connection
		} else {
			false
		}

	private fun doSql(sql: String): Int {
		val statement = connection.createStatement()
		return try {
			statement.executeUpdate(sql)
		} finally {
			commit()
			statement.closeOnCompletion()
		}
	}

	/**
	 * 创建表格
	 * @param table: 表格名
	 * @param keys: 属性列表
	 */
	override fun createTable(table: String, keys: Iterable<String>) {
		val sql = "CREATE TABLE if not exists $table (${keys.fieldStr()})"
		doSql(sql)
	}

	/**
	 * 根据提供的class对象自动化创建表格
	 * 但是有诸多缺陷，所以不是很建议使用
	 */
	override fun createTable(fields: Class<*>) {
		val sql = createTableStr(fields)
		doSql(sql)
	}

	/**
	 * 删除表格
	 */
	override fun deleteTable(table: String) {
		val sql = "DROP TABLE if exists $table"
		doSql(sql)
	}

	/**
	 * 删除表格
	 */
	override fun dropTable(table: String) {
		deleteTable(table)
	}

	/**
	 * 查询
	 * @param adapter 用于保存查询结果的数据类，由SQLAdapter继承而来
	 * @param fields 查询字段
	 * @param where 指定从一个表或多个表中获取数据的条件,Pair左边为字段名，右边为限定的值
	 * @param maxCount 最大查询数量
	 */
	override fun <T : Any> select(
		adapter: SqlAdapter<T>,
		fields: Iterable<String>?,
		where: Clause,
		order: Field?,
		reverse: Boolean,
		maxCount: Int?
	): SqlAdapter<T> =
		select(adapter, fields?.fieldStr() ?: "*", where.sqlStr, order?.fieldName, reverse, maxCount)

	override fun <T : Any> select(
		adapter: SqlAdapter<T>, fields: String, where: String?, order: String?, reverse: Boolean, maxCount: Int?
	): SqlAdapter<T> {
		val sql = "SELECT $fields FROM ${adapter.clazz.tableName
		}${if (where != null) " WHERE $where" else ""
		}${if (order != null) " ORDER BY $order ${if (reverse) "DESC" else "ASC"}" else ""
		}${if (maxCount != null) " limit 0,$maxCount" else ""
		};"
		val statement = connection.createStatement()
		try {
			adapter.adapt(
				statement.executeQuery(sql)
			)
		} catch (e: SQLiteException) {
			if (e.message != "[SQLITE_ERROR] SQL error or missing cn.tusom.database (no such table: ${adapter.clazz.tableName})") throw e
		}
		statement.closeOnCompletion()
		return adapter
	}

	private fun insert(connection: Connection, sql: String, table: Class<*>): Int {
		val statement = connection.createStatement()
		return try {
			statement.executeUpdate(sql)
		} catch (e: SQLiteException) {
			if (e.message == "[SQLITE_ERROR] SQL error or missing cn.tusom.database (no such table: ${table.tableName})") {
				createTable(table)
				statement.executeUpdate(sql)
			} else {
				throw e
			}
		} finally {
			connection.commit()
			statement.closeOnCompletion()
		}
	}

	override fun insert(value: Any): Int {
		val clazz = value.javaClass
		val fields = clazz.declaredFields
		val column = fields.fieldStr()
		val valueStr = fields.valueStr(value) ?: return 0
		val sql = "INSERT INTO ${value.tableName} ($column) VALUES ($valueStr);"
		return insert(connection, sql, clazz)
	}

	override fun insert(valueList: Iterable<*>): Int {
		val first = valueList.firstOrNull() ?: return 0
		val clazz = first.javaClass
		val fields = ArrayList<SqlFieldData>()
		clazz.declaredFields.forEach { field ->
			if (field.ignored) return@forEach
			val getter = field.getAnnotation(Getter::class.java)?.let { clazz.getDeclaredMethod(field.name) }
			fields.add(SqlFieldData(field, getter))
		}
		val values = fields.valueStr(valueList) ?: return 0
		if (values.isEmpty()) return 0
		val sql = "INSERT INTO ${first.tableName} (${clazz.declaredFields.fieldStr()}) VALUES $values;"
		return insert(connection, sql, clazz)
	}

	override fun insert(table: String, fields: String, values: String): Int {
		val sql = "INSERT INTO $table ($fields) VALUES $values;"
		return doSql(sql)
	}

	override fun update(table: String, set: String, where: String): Int {
		val sql = "UPDATE $table SET $set WHERE $where;"
		return doSql(sql)
	}

	override fun update(
		value: Any, where: Clause
	): Int {
		val set = StringBuilder()
		value.javaClass.declaredFields.forEach {
			it.isAccessible = true
			it.get(value)?.let { value ->
				set.append("${it.fieldName}=${value.fieldValue},")
			}
		}
		if (set.isNotEmpty()) {
			set.delete(set.length - 1, set.length)
		}
		return update(value.tableName, set.toString(), where.sqlStr)
	}

	override fun delete(table: String, where: String?): Int {
		val sql = "DELETE FROM $table${if (where?.isNotEmpty() == true) " WHERE $where" else ""};"
		return doSql(sql)
	}

	override fun delete(table: String, where: Clause?): Int {
		return delete(table, where?.sqlStr)
	}

	override fun commit() {
		synchronized(connection) {
			connection.commit()
		}
	}

	override fun close() {
		synchronized(connectionMap) {
			connectionCount[path] = connectionCount[path] ?: 1 - 1
			if (connectionCount[path] == 0) {
				connectionCount.remove(path)
				connectionMap.remove(path)
				connection.close()
			}
		}
	}

	override fun hashCode(): Int {
		var result = connection.hashCode()
		result = 31 * result + path.hashCode()
		return result
	}

	class CantConnectDataBase(s: String? = null) : SQLException(s)

	companion object {
		private val connectionMap by lazy {
			Class.forName("org.sqlite.JDBC")
			HashMap<String, Connection>()
		}
		private var connectionCount = HashMap<String, Int>()

		private val Field.fieldType: String?
			get() = when (type) {
				java.lang.Byte::class.java -> "INTEGER"
				java.lang.Short::class.java -> "INTEGER"
				java.lang.Integer::class.java -> "INTEGER"
				java.lang.Long::class.java -> "INTEGER"
				java.lang.Float::class.java -> "REAL"
				java.lang.Double::class.java -> "REAL"

				Byte::class.java -> "INTEGER"
				Char::class.java -> "INTEGER"
				Short::class.java -> "INTEGER"
				Int::class.java -> "INTEGER"
				Long::class.java -> "INTEGER"
				Float::class.java -> "REAL"
				Double::class.java -> "REAL"

				java.lang.String::class.java -> getAnnotation<TextLength>()?.let { "CHAR(${it.length})" } ?: "TEXT"

				else -> {
					if (type.isSqlField) {
						type.getAnnotation<FieldType>()?.name ?: type.name.split('.').last()
					} else {
						null
					}
				}
			}

		private val regexp = object : org.sqlite.Function() {
			override fun xFunc() {
				val regex = Regex(value_text(0) ?: "")
				val value = value_text(1) ?: ""
				result(if (regex.containsMatchIn(value)) 1 else 0)
			}
		}

		private fun StringBuilder.appendField(
			field: Field,
			foreignKeyList: java.util.AbstractCollection<in Pair<String, String>>
		) {
			val fieldName = field.fieldName
			append("`$fieldName` ${field.fieldType ?: return}")
			field.annotations.forEach annotations@{ annotation ->
				append(" ${when (annotation) {
					is NotNull -> "NOT NULL"
					is Unique -> "UNIQUE"
					is Default -> "DEFAULT ${annotation.default}"
					is Check -> "CHECK(${field.fieldName}${annotation.func})"
					is ExtraAttribute -> annotation.attributes
					is PrimaryKey -> " PRIMARY KEY${field.getAnnotation(AutoIncrement::class.java)?.let { " AUTOINCREMENT" }
						?: ""}"
					is ForeignKey -> {
						foreignKeyList.add(fieldName to if (annotation.target.isNotEmpty()) annotation.target else fieldName)
						return@annotations
					}
					else -> return@annotations
				}}")
			}
			append(',')
		}

		fun createTableStr(keys: Class<*>): String {
			val foreignKey = keys.getAnnotation(ForeignKey::class.java)?.let {
				if (it.target.isNotEmpty()) it.target else null
			}
			val foreignKeyList = ArrayList<Pair<String, String>>()

			val valueStrBuilder = StringBuilder()
			valueStrBuilder.append("CREATE TABLE IF NOT EXISTS ${keys.tableName}(")
			keys.declaredFields.forEach {
				valueStrBuilder.appendField(it, foreignKeyList)
			}

			foreignKey?.let {
				if (foreignKeyList.isEmpty()) return@let
				val (source, target) = foreignKeyList.fieldStr()
				valueStrBuilder.append("FOREIGN KEY ($source) REFERENCES $it ($target),")
			}

			valueStrBuilder.deleteCharAt(valueStrBuilder.length - 1)
			valueStrBuilder.append(");")
			return valueStrBuilder.toString()
		}
	}
}
