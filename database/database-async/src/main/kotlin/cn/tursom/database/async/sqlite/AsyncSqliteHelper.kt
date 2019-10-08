package cn.tursom.database.async.sqlite

import cn.tursom.core.simplifyPath
import cn.tursom.database.SqlFieldData
import cn.tursom.database.SqlUtils.fieldName
import cn.tursom.database.SqlUtils.fieldStr
import cn.tursom.database.SqlUtils.fieldValue
import cn.tursom.database.SqlUtils.getAnnotation
import cn.tursom.database.SqlUtils.ignored
import cn.tursom.database.SqlUtils.tableName
import cn.tursom.database.SqlUtils.valueStr
import cn.tursom.database.annotation.*
import cn.tursom.database.async.AsyncSqlAdapter
import cn.tursom.database.async.AsyncSqlHelper
import cn.tursom.database.async.vertx
import cn.tursom.database.clauses.Clause
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLConnection
import kotlinx.coroutines.runBlocking
import org.sqlite.SQLiteException
import java.io.File
import java.lang.reflect.Field
import java.sql.Connection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AsyncSqliteHelper(base: String) : AsyncSqlHelper {
	private val path = File(base).absolutePath.simplifyPath()

	override val connection = runBlocking {
		val config = JsonObject()
		config.put("url", "jdbc:sqlite:$path")
		config.put("driver_class", "org.sqlite.JDBC")
		suspendCoroutine<SQLConnection> { cont ->
			JDBCClient.createShared(vertx, config, File(base).absolutePath.replace(".${File.separator}", "")).getConnection {
				if (!it.failed()) {
					val conn = it.result()

					// 我们要利用反射强行把我们需要的方法注入进去
					val connField = conn.javaClass.getDeclaredField("conn")
					connField.isAccessible = true
					val fieldConn = connField.get(conn)
					val innerField = fieldConn.javaClass.getDeclaredField("inner")
					innerField.isAccessible = true
					org.sqlite.Function.create(innerField.get(fieldConn) as Connection, "REGEXP", regexp)

					cont.resume(conn)
				} else {
					cont.resumeWithException(it.cause())
				}
			}
		}
	}

	override suspend fun doSql(sql: String): Int = suspendCoroutine { cont ->
		connection.execute(sql) {
			if (it.succeeded()) {
				cont.resume(1)
			} else {
				cont.resumeWithException(it.cause())
			}
		}
	}

	override suspend fun commit() = suspendCoroutine<Unit> { cont -> connection.commit { cont.resume(Unit) } }

	suspend fun createTable(fields: Class<*>, table: String) {
		val sql = createTableStr(fields, table)
		doSql(sql)
	}

	override suspend fun createTable(fields: Class<*>) {
		createTable(fields, fields.tableName)
	}

	override suspend fun deleteTable(table: String) {
		val sql = "DROP TABLE if exists $table"
		doSql(sql)
	}

	override suspend fun dropTable(table: String) {
		deleteTable(table)
	}

	override suspend fun <T : Any> select(
		adapter: AsyncSqlAdapter<T>,
		fields: Iterable<String>,
		where: Clause?,
		order: Field?,
		reverse: Boolean,
		maxCount: Int?,
		table: String
	): AsyncSqlAdapter<T> =
		select(adapter, fields.fieldStr(), where?.sqlStr, order?.fieldName, reverse, maxCount)

	override suspend fun <T : Any> select(
		adapter: AsyncSqlAdapter<T>,
		fields: String,
		where: String?,
		order: String?,
		reverse: Boolean,
		maxCount: Int?,
		table: String
	): AsyncSqlAdapter<T> {
		val sql = "SELECT $fields FROM ${table
		}${if (where != null) " WHERE $where" else ""
		}${if (order != null) " ORDER BY $order ${if (reverse) "DESC" else "ASC"}" else ""
		}${if (maxCount != null) " limit $maxCount" else ""
		};"
		return try {
			suspendCoroutine { cont ->
				connection.query(sql) {
					if (it.succeeded()) {
						adapter.adapt(it.result()!!)
						cont.resume(adapter)
					} else {
						cont.resumeWithException(it.cause())
					}
				}
			}
		} catch (e: SQLiteException) {
			if (e.message != "[SQLITE_ERROR] SQL error or missing cn.tusom.database (no such table: ${adapter.clazz.tableName})") throw e
			createTable(adapter.clazz)
			adapter
		}
	}

	private suspend fun insert(sql: String, table: Class<*>, tableName: String): Int {
		@Suppress("DuplicatedCode")
		return try {
			doSql(sql)
		} catch (e: Throwable) {
			createTable(table, tableName)
			doSql(sql)
		}
	}

	override suspend fun insert(value: Any): Int {
		val clazz = value.javaClass
		val fields = clazz.declaredFields
		val column = fields.fieldStr()
		val valueStr = fields.valueStr(value) ?: return 0
		val sql = "INSERT INTO ${value.tableName} ($column) VALUES ($valueStr);"
		return insert(sql, clazz, clazz.tableName)
	}

	@Suppress("DuplicatedCode")
	override suspend fun insert(valueList: Iterable<*>): Int {
		val first = valueList.firstOrNull() ?: return 0
		val clazz = first.javaClass
		val fields = ArrayList<SqlFieldData>()
		clazz.declaredFields.forEach { field ->
			if (field.ignored) return@forEach
			val getter = field.getAnnotation(Getter::class.java)?.let { clazz.getDeclaredMethod(it.getter) }
			fields.add(SqlFieldData(field, getter))
		}
		val values = fields.valueStr(valueList) ?: return 0
		if (values.isEmpty()) return 0
		val sql = "INSERT INTO ${first.tableName} (${clazz.declaredFields.fieldStr()}) VALUES $values;"
		return insert(sql, clazz, clazz.tableName)
	}

	override suspend fun replace(table: String, value: Any): Int {
		val clazz = value.javaClass
		val fields = clazz.declaredFields
		val column = fields.fieldStr()
		val valueStr = fields.valueStr(value) ?: return 0
		val sql = "REPLACE INTO $table ($column) VALUES ($valueStr);"
		return insert(sql, clazz, table)
	}

	@Suppress("DuplicatedCode")
	override suspend fun replace(table: String, valueList: Iterable<*>): Int {
		val first = valueList.firstOrNull() ?: return 0
		val clazz = first.javaClass
		val fields = ArrayList<SqlFieldData>()
		clazz.declaredFields.forEach { field ->
			if (field.ignored) return@forEach
			val getter = field.getAnnotation(Getter::class.java)?.let { clazz.getDeclaredMethod(it.getter) }
			fields.add(SqlFieldData(field, getter))
		}
		val values = fields.valueStr(valueList) ?: return 0
		if (values.isEmpty()) return 0
		val sql = "REPLACE INTO $table (${clazz.declaredFields.fieldStr()}) VALUES $values;"
		return insert(sql, clazz, table)
	}

	override suspend fun update(
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
		val sql = "UPDATE ${value.tableName} SET $set WHERE ${where.sqlStr};"
		return doSql(sql)
	}

	override suspend fun update(table: String, set: String, where: String?): Int {
		val sql = "UPDATE $table SET $set${if (where != null && where.isNotEmpty()) " WHERE $where" else ""};"
		return doSql(sql)
	}

	override suspend fun delete(table: String, where: String?): Int {
		val sql = "DELETE FROM $table${if (where?.isNotEmpty() == true) " WHERE $where" else ""};"
		return doSql(sql)
	}

	override suspend fun delete(table: String, where: Clause?): Int {
		return delete(table, where?.sqlStr)
	}

	override suspend fun close() {
		connection.close()
	}

	companion object {
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
					getAnnotation<FieldType>()?.name ?: type.getAnnotation<FieldType>()?.name ?: type.name.split('.').last()
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
			if (field.ignored) return
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

		fun createTableStr(keys: Class<*>, table: String): String {
			val foreignKey = keys.getAnnotation(ForeignKey::class.java)?.let {
				if (it.target.isNotEmpty()) it.target else null
			}
			val foreignKeyList = ArrayList<Pair<String, String>>()

			val valueStrBuilder = StringBuilder()
			valueStrBuilder.append("CREATE TABLE IF NOT EXISTS $table(")
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